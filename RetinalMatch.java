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
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Core.MinMaxLocResult;
import org.opencv.core.Scalar;
import java.util.List;
import java.util.ArrayList;

import org.opencv.core.MatOfInt;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfPoint;

public class RetinalMatch {
    public static void main(String[] args) {
        // load the OpenCV native library
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        //String location = "RIDB/IM000002_18.jpg";
        
        // get input from user for files
        String location1 = args[0];
        String location2 = args[1];

        // get the jpg images from the internal resource folder
        Mat image1 = Imgcodecs.imread(location1);
        Mat image2 = Imgcodecs.imread(location2);

        // crop to roughly the eye
        Rect crop = new Rect(180, 0, 1080, 1000);
        image1 = new Mat(image1, crop);
        image2 = new Mat(image2, crop);

        // convert the images to gray scale
        Imgproc.cvtColor(image1, image1, Imgproc.COLOR_BGR2GRAY);
        Imgproc.cvtColor(image2, image2, Imgproc.COLOR_BGR2GRAY);

        // autocontrast histogram equalisation
        Imgproc.equalizeHist(image1, image1);
        Imgcodecs.imwrite("ImageEq1.jpg", image1);
        Imgproc.equalizeHist(image2, image2);
        Imgcodecs.imwrite("ImageEq2.jpg", image2);

        //blur images
        Imgproc.GaussianBlur(image1, image1, new Size(7, 7), 1.3);
        Imgcodecs.imwrite("ImageBl1.jpg", image1);
        Imgproc.GaussianBlur(image2, image2, new Size(7, 7), 1.3);
        Imgcodecs.imwrite("ImageBl2.jpg", image2);

        // edge detection
        Imgproc.Canny(image1, image1, 45, 45*3, 3, false);
        Imgcodecs.imwrite("ImageEd1.jpg", image1);
        Imgproc.Canny(image2, image2, 45, 45*3, 3, false);
        Imgcodecs.imwrite("ImageEd2.jpg", image2);

        // thresholding
        Imgproc.adaptiveThreshold(image1, image1, 255, Imgproc.ADAPTIVE_THRESH_MEAN_C, Imgproc.THRESH_BINARY, 5, 20);
        Imgcodecs.imwrite("ImageTh1.jpg", image1);
        Imgproc.adaptiveThreshold(image2, image2, 255, Imgproc.ADAPTIVE_THRESH_MEAN_C, Imgproc.THRESH_BINARY, 5, 20);
        Imgcodecs.imwrite("ImageTh2.jpg", image2);

        // morpholical operations
        int kernelSize = 1;
        int elementType = Imgproc.CV_SHAPE_RECT;
        Mat element = Imgproc.getStructuringElement(elementType, new Size(2 * kernelSize + 1, 2 * kernelSize + 1),
            new Point(kernelSize, kernelSize));
        Imgproc.erode(image1, image1, element);
        Imgcodecs.imwrite("ImageF1.jpg", image1);
        Imgproc.erode(image2, image2, element);
        Imgcodecs.imwrite("ImageF2.jpg", image2);

        // matching
        float[] range = {0, 256};
        MatOfFloat histRange = new MatOfFloat(range);

        /*
        Mat hist1 = new Mat();
        List<Mat> imageList1 = new ArrayList<>();
        imageList1.add(image1);
        Imgproc.calcHist(imageList1, new MatOfInt(0), new Mat(), hist1, new MatOfInt(256), histRange);
        */

        Mat hist2 = new Mat();
        List<Mat> imageList2 = new ArrayList<>();
        imageList2.add(image2);
        Imgproc.calcHist(imageList2, new MatOfInt(0), new Mat(), hist2, new MatOfInt(10), histRange);
        Core.normalize(hist2, hist2, 0, 1, Core.NORM_MINMAX);

        Mat imageCropped = new Mat();
        Mat hist1 = new Mat();
        List<Mat> imageList1 = new ArrayList<>();

        for(int i = 0; i < 900; i += 180) {
            for(int j = 0; j < 900; j += 100) {
                crop = new Rect(i, i+100, j, j+100);
                System.out.println(i);
                System.out.println(j);
                imageCropped = new Mat(image1, crop);
                imageList1.add(imageCropped);
                Imgproc.calcHist(imageList1, new MatOfInt(0), new Mat(), hist1, new MatOfInt(10), histRange);
                Core.normalize(hist1, hist1, 0, 1, Core.NORM_MINMAX);
                imageList1.clear();
                System.out.println(Imgproc.compareHist(hist1, hist2, 0));
            }
        }

        //Mat histImage = new Mat();
        //Core.normalize(hist1, hist1, 0, 1, Core.NORM_MINMAX);
        //System.out.println(Imgproc.compareHist(hist1, hist2, 0));
    }
}