import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.core.Size;
import org.opencv.core.MatOfInt;
import org.opencv.core.MatOfFloat;
import org.opencv.highgui.HighGui;
import org.opencv.core.Scalar;
import java.util.*;


public class RetinalMatch {
    
    public static void main(String[] args) {
        
        // load the OpenCV native library
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        //take two images of same person
        Mat base = Imgcodecs.imread("RIDB/RIDB/IM000001_1.jpg");
        Mat test = Imgcodecs.imread("RIDB/RIDB/IM000003_2.jpg");

        //create a copy of base for control tests
        Mat baseCopy = Imgcodecs.imread("RIDB/RIDB/IM000001_1.jpg");

        //remove noise from images
        Imgproc.GaussianBlur( base, base, new Size(3, 3), 0, 0, Core.BORDER_DEFAULT );
        Imgproc.GaussianBlur( test, test, new Size(3, 3), 0, 0, Core.BORDER_DEFAULT );

        //convert images to greyscale
        Imgproc.cvtColor(base, base, Imgproc.COLOR_BGR2GRAY);
        Imgproc.cvtColor(test, test, Imgproc.COLOR_BGR2GRAY);

        //equalise image histograms
        Mat eqBase = new Mat();
        Mat eqTest = new Mat();
        Imgproc.equalizeHist(base, eqBase);
        Imgproc.equalizeHist(test, eqTest);
        
        //create mask that only leaves veins
        Imgproc.Canny(eqBase, eqBase, 100, 200);

        //compute x and y gradients
        int scale = 1;
        int delta = 0;
        int ddepth = CvType.CV_16S;

        Mat grad_x = new Mat(), grad_y = new Mat();
        Mat abs_grad_x = new Mat(), abs_grad_y = new Mat();
        Imgproc.Sobel( eqBase, grad_x, ddepth, 1, 0, 3, scale, delta, Core.BORDER_DEFAULT );       
        Imgproc.Sobel( eqBase, grad_y, ddepth, 0, 1, 3, scale, delta, Core.BORDER_DEFAULT );

        //now that we have gradients, turn them into a histogram for comparison
        //final gradient magnitude = square root of Gx^2 + Gy^2

        //display altered image
        HighGui.imshow( "Equalized Image", eqBase );
        HighGui.waitKey(0);
        System.exit(0);
        

    }

    //turns images into histograms and compares them
    public static void compareHist(Mat base, Mat test, Mat copy){

        //convert images to HSV format
        Mat hsvBase = new Mat();
        Mat hsvTest = new Mat();
        Mat hsvCopy = new Mat();
        Imgproc.cvtColor( base, hsvBase, Imgproc.COLOR_BGR2HSV);
        Imgproc.cvtColor( test, hsvTest, Imgproc.COLOR_BGR2HSV);
        Imgproc.cvtColor( copy, hsvCopy, Imgproc.COLOR_BGR2HSV);

        //initialize args for histograms
        int hBins = 50, sBins = 60;
        int[] histSize = { hBins, sBins };
        // hue varies from 0 to 179, saturation from 0 to 255
        float[] ranges = { 0, 180, 0, 256 };
        // Use 0 and 1 channels
        int[] channels = { 0, 1 };

        //create histogram for base, test, and copy images
        Mat histBase = new Mat();

        List<Mat> hsvBaseList = Arrays.asList(hsvBase);
        Imgproc.calcHist(hsvBaseList, new MatOfInt(channels), new Mat(), histBase, new MatOfInt(histSize), new MatOfFloat(ranges), false);
        Core.normalize(histBase, histBase, 0, 1, Core.NORM_MINMAX);

        Mat histTest = new Mat();

        List<Mat> hsvTestList = Arrays.asList(hsvTest);
        Imgproc.calcHist(hsvTestList, new MatOfInt(channels), new Mat(), histTest, new MatOfInt(histSize), new MatOfFloat(ranges), false);
        Core.normalize(histTest, histTest, 0, 1, Core.NORM_MINMAX);

        Mat histCopy = new Mat();

        List<Mat> hsvCopyList = Arrays.asList(hsvCopy);
        Imgproc.calcHist(hsvCopyList, new MatOfInt(channels), new Mat(), histCopy, new MatOfInt(histSize), new MatOfFloat(ranges), false);
        Core.normalize(histCopy, histCopy, 0, 1, Core.NORM_MINMAX);

        //apply the four comparison methods
        for(int i = 0; i < 4; i++){
            //base to test image
            double compareTest = Imgproc.compareHist(histBase, histTest, i);
            //base to copy image
            double compareCopy = Imgproc.compareHist(histBase, histCopy, i);
            
            System.out.println(i + " Base to test: " + compareTest);
            System.out.println(i + " Base to copy: " + compareCopy);
        }
    }
}
