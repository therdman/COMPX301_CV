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
        String location = "RIDB/IM000002_18.jpg";
        System.out.print("Convert the image at " + location + " in gray scale... ");
        // get the jpeg image from the internal resource folder
        Mat image = Imgcodecs.imread(location);
        // crop
        Rect crop = new Rect(180, 0, 1100, 1000);
        image = new Mat(image, crop);
        // convert the image in gray scale
        Imgproc.cvtColor(image, image, Imgproc.COLOR_BGR2GRAY);


        // autocontrast histogram equalisation
        Mat equalisedImage = new Mat(image.rows(), image.cols(), image.type());
        Imgproc.equalizeHist(image, equalisedImage);
        Imgcodecs.imwrite("TestEqualised.jpg", equalisedImage);

        //blur
        Mat blurredImage = new Mat(image.rows(), image.cols(), image.type());
        Mat result = blurredImage;
        Imgproc.GaussianBlur(equalisedImage, blurredImage, new Size(7, 7), 1.3);
        Imgproc.Canny(blurredImage, blurredImage, 40, 40*3, 3, false);
        Imgcodecs.imwrite("TestBlurred.jpg", blurredImage);

        // thresholding
        Imgproc.adaptiveThreshold(blurredImage, result, 255, Imgproc.ADAPTIVE_THRESH_MEAN_C, Imgproc.THRESH_BINARY, 5, 20);
        Imgcodecs.imwrite("TestThresh1.jpg", result);

        // morpholical operations
        Mat eroeded = new Mat(image.rows(), image.cols(), image.type());
        int kernelSize = 1;
        int elementType = Imgproc.CV_SHAPE_RECT;
        Mat element = Imgproc.getStructuringElement(elementType, new Size(2 * kernelSize + 1, 2 * kernelSize + 1),
            new Point(kernelSize, kernelSize));
        Imgproc.erode(result, eroeded, element);
        Imgcodecs.imwrite("TestEroed2.jpg", eroeded);
    }
}