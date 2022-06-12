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

public class RetinalMatch {
    public static void main(String[] args) {
        // load the OpenCV native library
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        // create and print on screen a 3x3 identity matrix
        System.out.println("Create a 3x3 identity matrix...");
        Mat mat = Mat.eye(3, 3, CvType.CV_8UC1);
        System.out.println("mat = " + mat.dump());

        // prepare to convert a RGB image in gray scale
        String location = "RIDB/IM000003_20.jpg";
        System.out.print("Convert the image at " + location + " in gray scale... ");
        // get the jpeg image from the internal resource folder
        Mat image = Imgcodecs.imread(location);
        // convert the image in gray scale
        Imgproc.cvtColor(image, image, Imgproc.COLOR_BGR2GRAY);

        // my stuff
        // compress image test
        //MatOfInt par = new MatOfInt(Imgcodecs.IMWRITE_JPEG_QUALITY, 50);
        //Imgcodecs.imwrite("IM000001_1-grey-compressed.jpg", image, par);

        // write the new image on disk
        Imgcodecs.imwrite("IM000001_1-grey.jpg", image);
        System.out.println("Done!");


        // autocontrast histogram equalisation
        Mat equalisedImage = new Mat(image.rows(), image.cols(), image.type());
        Imgproc.equalizeHist(image, equalisedImage);
        Imgcodecs.imwrite("TestEqualised.jpg", equalisedImage);

        //blur
        Mat blurredImage = new Mat(image.rows(), image.cols(), image.type());
        Mat result = blurredImage;
        Imgproc.GaussianBlur(equalisedImage, blurredImage, new Size(7, 7), 0.1);
        Imgproc.Canny(blurredImage, blurredImage, 50, 50*3, 3, false);
        Imgcodecs.imwrite("TestBlurred.jpg", blurredImage);

        // thresholding
        Imgproc.adaptiveThreshold(blurredImage, result, 255, Imgproc.ADAPTIVE_THRESH_MEAN_C, Imgproc.THRESH_BINARY, 15, 40);
        Imgcodecs.imwrite("TestThresh.jpg", result);
    }
}