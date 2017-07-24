package sample.model;

import org.opencv.core.Mat;
import org.opencv.core.Point;
import sample.EndPoints;
import sample.PointWeight;
import sample.SkeletonPoints;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by oleh_pi on 10.11.2016.
 */
public class PixelArray {

    public int blackPixelCount = 0;
    public int allPixelsCount = 0;
    public Mat src;
    private int imgRows = 0;
    private int imgCols = 0;
    private double picdata[][];


    public PixelArray(Mat src){

        this.src = src;
        imgRows = src.rows();
        imgCols = src.cols();

    }

    public Mat calculatePixels(){

        Mat dst = new Mat(imgRows, imgCols, 3);

        byte buff[] = new byte[ (int) (this.src.total() * this.src.channels())];

        int a,b;

        picdata =  new double[imgRows][imgCols] ;
        double[] temp;

        for (a=0 ; a<imgRows;a++)
        {
            for (b=0 ; b<imgCols;b++)
            {
                temp = src.get(a, b);
                //System.out.println(temp);
                picdata[a][b]=temp[0];
                //System.out.println(picdata[a][b]);
                dst.put(a,b,picdata[a][b]);

                allPixelsCount++;
            }
        }

        return dst;
    }

    public List <SkeletonPoints> getBlackPixelCount(){

        List <SkeletonPoints> skeleton_points = new ArrayList<SkeletonPoints>();

        for (int i = 0; i< imgRows; i++) {
            for (int j = 0; j < imgCols; j++) {
                if(picdata[i][j] < 10){

                    skeleton_points.add(new SkeletonPoints(i,j));

                    blackPixelCount++;
                }
            }
        }
        return skeleton_points;
    }


    /**
     * список кінцевих точок
     * @return List <EndPoints>
     */
    public List <EndPoints> getEndPointss() {

        List <EndPoints> end_points = new ArrayList<EndPoints>();

        for (int i = 0; i < imgRows; i++) {
            for (int j = 0; j < imgCols; j++) {
                if (picdata[i][j] < 100) {

                    int temp_i=i;int temp_j=j;

                    int blackPixelCount = 0;

                    if(picdata[temp_i-1][temp_j] < 100)
                    {
                        blackPixelCount++;
                    }
                    if(picdata[temp_i+1][temp_j] < 100)
                    {
                        blackPixelCount++;
                    }
                    if(picdata[temp_i][temp_j-1] < 100)
                    {
                        blackPixelCount++;
                    }
                    if(picdata[temp_i][temp_j+1] < 100)
                    {
                        blackPixelCount++;
                    }
                    if(picdata[temp_i+1][temp_j+1] < 100)
                    {
                        blackPixelCount++;
                    }
                    if(picdata[temp_i-1][temp_j-1] < 100)
                    {
                        blackPixelCount++;
                    }
                    if(picdata[temp_i-1][temp_j+1] < 100)
                    {
                        blackPixelCount++;
                    }
                    if(picdata[temp_i+1][temp_j-1] < 100)
                    {
                        blackPixelCount++;
                    }

                    if(blackPixelCount < 2){
                        end_points.add(new EndPoints(i,j));
                        //System.out.println("кінцеві " + i + " _ " + j);
                    }
                }
            }
        }

        return end_points;
    }

    /**
     * список внутрішніх точок (вузлів)
     * @return List <EndPoints>
     */
    public List <EndPoints> getInnerPoints(){

        List <EndPoints> innerPoints = new ArrayList<EndPoints>();

        for (int i = 0; i< imgRows; i++) {
            for (int j = 0; j < imgCols; j++) {
                if(picdata[i][j] < 100){
                    int temp_i=i;int temp_j=j;
                    if(picdata[temp_i][temp_j+1] < 100 && picdata[temp_i-1][temp_j] < 100 ||
                            picdata[temp_i][temp_j-1] < 100 && picdata[temp_i-1][temp_j] < 100){

                        innerPoints.add(new EndPoints(temp_i, temp_j));
                        //System.out.println(temp_i + " _ " + temp_j);
                    }
                }
            }
        }

        return innerPoints;
    }


    /**
     * сума ваг кінцевих точок
     * @param points_weights - список пікселів скелетону
     * @param x - вхідна координати X для порівнняя
     * @param y - вхідна координати Y для порівнняя
     * @return - сума ваг 2 точок
     */
    private int sumOFWeight(List <PointWeight> points_weights, int x, int y){

        int dist =1;
        for(int i = 0; i < points_weights.size(); i++){
            if(points_weights.get(i).y == x && points_weights.get(i).x == y){
                //System.out.println(points_weights.get(i).y +" ==== "+ x +" || " + points_weights.get(i).x + " " + y);
                dist = points_weights.get(i).distance;
            }
        }

        return dist;
    }

    public void estimateWeightOfSkeleton(List<EndPoints> innerPoints, List<EndPoints> endPoints,
                                         List <PointWeight> points_weights){



        // пошук крайнього мін значення (внутрішньої точки)
        int min_right_X = innerPoints.get(0).x; int min_right_Y=innerPoints.get(0).y;
        for(int i=0; i < innerPoints.size(); i++){
            if(innerPoints.get(i).y < min_right_Y){
                min_right_X = innerPoints.get(i).y; min_right_Y= innerPoints.get(i).x;
            }
        }
        System.out.println("left ********************************************************");
        for(int i = 0; i < endPoints.size(); i++){
            if(endPoints.get(i).y < min_right_X){
                System.out.println(min_right_X + " | " + min_right_Y + "   =>    " + endPoints.get(i).y + " | " + endPoints.get(i).x );
                System.out.println(this.EuclidianDistance(min_right_X, min_right_Y, endPoints.get(i).y, endPoints.get(i).x )*
                        (
                                sumOFWeight(points_weights, min_right_X,min_right_Y ) +
                                        sumOFWeight(points_weights, endPoints.get(i).y,endPoints.get(i).x )
                        ));
            }
        }
        System.out.println("left ********************************************************");
        /*
        for(int i = 0; i < endPoints.size(); i++){


            if(endPoints.get(i).y < innerPoints.get(0).y){
                System.out.println(innerPoints.get(0).y + " = dd " + innerPoints.get(0).x + "||||" + endPoints.get(i)  );
                System.out.println(this.EuclidianDistance(innerPoints.get(0).y, innerPoints.get(0).x, endPoints.get(i).y,
                        endPoints.get(i).x )*(
                                sumOFWeight(points_weights, innerPoints.get(0).y,innerPoints.get(0).x ) +
                                        sumOFWeight(points_weights, endPoints.get(i).y,endPoints.get(i).x )
                ));
            }
            /*
            if(endPoints.get(i).y > innerPoints.get(innerPoints.size()-1).y){
                System.out.println(innerPoints.get(innerPoints.size()-1).y + " = dd " + innerPoints.get(innerPoints.size()-1).x + "||||" + endPoints.get(i)  );
                System.out.println(this.EuclidianDistance(innerPoints.get(innerPoints.size()-1).y, innerPoints.get(innerPoints.size()-1).x, endPoints.get(i).y, endPoints.get(i).x ));
            }
            */
/*
        }*/

        // між внутрішніми точками (вузлами)
        System.out.println("-----------------------------------------------");

        try {
            int j = 0;
            for(int i = 0; i < endPoints.size()-1; i++){
                j=i+1;

                System.out.println(innerPoints.get(i).y + " | " + innerPoints.get(i).x + "   =>   " + innerPoints.get(j).y + " | " + innerPoints.get(j).x );
                System.out.println(this.EuclidianDistance(innerPoints.get(i).y, innerPoints.get(i).x,
                        innerPoints.get(j).y, innerPoints.get(j).x )*(
                        sumOFWeight(points_weights, innerPoints.get(j).y,innerPoints.get(j).x ) +
                                sumOFWeight(points_weights, innerPoints.get(i).y,innerPoints.get(i).x )
                ));

                //якщо між 2 внутрішніми точками є гілки
                for(int u = 0; u < endPoints.size()-1; u++){
                    if(endPoints.get(u).y > innerPoints.get(i).y && endPoints.get(u).y < innerPoints.get(j).y){
                        System.out.println("+++++++++++++++++++++++++++++++++");
                        System.out.println(innerPoints.get(i).y + " | " + innerPoints.get(i).x + "   =>   " +
                                endPoints.get(u).y + " | " + endPoints.get(u).x);

                        System.out.println(this.EuclidianDistance(innerPoints.get(i).y, innerPoints.get(i).x,
                                endPoints.get(u).y, endPoints.get(u).x )*(
                                sumOFWeight(points_weights, innerPoints.get(i).y,innerPoints.get(i).x ) +
                                        sumOFWeight(points_weights, endPoints.get(u).y,endPoints.get(u).x )
                        ));
                        System.out.println("+++++++++++++++++++++++++++++++++");
                    }
                }

            }
        }catch (Exception e){}



        // пошук макс правого вузла
        int max_right_X = innerPoints.get(0).x; int max_right_Y=innerPoints.get(0).y;
        for(int i=0; i < innerPoints.size(); i++){
            if(innerPoints.get(i).y > max_right_Y){
                max_right_X = innerPoints.get(i).y; max_right_Y= innerPoints.get(i).x;
            }
        }

        System.out.println("right ********************************************************");
        for(int i = 0; i < endPoints.size(); i++){
            if(endPoints.get(i).y > max_right_X){
                System.out.println(max_right_Y + " | " + max_right_X + "   =>   " + endPoints.get(i).y + " | " +  endPoints.get(i).x);
                System.out.println(this.EuclidianDistance(max_right_Y, max_right_X, endPoints.get(i).y, endPoints.get(i).x )*
                        (
                        sumOFWeight(points_weights, max_right_Y,max_right_X ) +
                                sumOFWeight(points_weights, endPoints.get(i).y,endPoints.get(i).x )
                ));
            }
        }

    }


    private double EuclidianDistance(int x1, int y1, int x2, int y2){

        return Math.sqrt( Math.pow((x1-y1),2) + Math.pow((x2-y2),2));

    }


    public List <PointWeight> getWeight(List <SkeletonPoints> sk_points){

        int top_count = 0;
        int bottom_count = 0;
        int righr_count = 0;
        int left_count = 0;

        List <PointWeight> points_weight = new ArrayList<PointWeight>();



        for(int i = 0; i < sk_points.size(); i++){
            List<Integer> sides = new ArrayList<Integer>();

            top_count = this.topSide(sk_points.get(i).x, sk_points.get(i).y);
            bottom_count = this.bottomSide(sk_points.get(i).x, sk_points.get(i).y);
            righr_count = this.rightSide(sk_points.get(i).x, sk_points.get(i).y);
            left_count = this.leftSide(sk_points.get(i).x, sk_points.get(i).y);

            if(top_count != 0){
                sides.add(top_count);
            }
            if(bottom_count !=0){
                sides.add(bottom_count);
            }
            if(righr_count != 0){
                sides.add(righr_count);
            }
            if(left_count != 0){
                sides.add(left_count);
            }

            int min = sides.get(0);
            for(int h=0; h<sides.size(); h++){
                if(sides.get(h) < min){
                    min = sides.get(h);
                }
            }

            points_weight.add(new PointWeight(sk_points.get(i).x, sk_points.get(i).y, min));
        }


        return points_weight;
    }





    private int topSide(int X, int Y){
        int dist=0;

        try {
            for(int i=0; i<200; i++){

                if(picdata[X--][Y] < 100){
                    dist++;
                }
            }
        }catch (Exception e){
            //System.out.println(e);
        }
        return dist;
    }

    private int bottomSide(int X, int Y){
        int dist=0;

        try {
            for(int i=0; i<170; i++){

                if(picdata[X++][Y] < 100){
                    dist++;
                }
            }
        }catch (Exception e){
            //System.out.println(e);
        }
        return dist;
    }

    private int rightSide(int X, int Y){
        int dist=0;

        try {
            for(int i=0; i<170; i++){

                if(picdata[X][Y++] < 100){
                    dist++;
                }
            }
        }catch (Exception e){
            //System.out.println(e);
        }
        return dist;
    }

    private int leftSide(int X, int Y){
        int dist=0;

        try {
            for(int i=0; i<170; i++){

                if(picdata[X][Y--] < 100){
                    dist++;
                }
            }
        }catch (Exception e){
            //System.out.println(e);
        }
        return dist;
    }


    public int getAllPixelsCount(){
        return allPixelsCount;
    }

    public Mat getDifference(Mat img1, Mat img2){
        Mat dst = new Mat(img1.rows(), img1.cols(), 3);

        byte buffEtalon[] = new byte[ (int) (img1.total() * img1.channels())];
        byte buffSegmented[] = new byte[ (int) (img2.total() * img2.channels())];

        int a,b;

        double picdataEtalon[][] =  new double[img1.rows()][img1.cols()] ;
        double picdataSegmented[][] =  new double[img2.rows()][img2.cols()] ;

        double[] tempEtalon;
        double[] tempSegmented;

        for (a=0 ; a < img1.rows(); a++)
        {
            for (b=0 ; b < img1.cols(); b++)
            {
                tempEtalon = img1.get(a, b);
                tempSegmented = img2.get(a, b);

                picdataEtalon[a][b]=tempEtalon[0];
                picdataSegmented[a][b]=tempSegmented[0];


                if(picdataEtalon[a][b] == picdataSegmented[a][b]){
                    dst.put(a,b,255);
                }else{
                    dst.put(a,b,0);
                }
            }
        }

        return dst;
    }
}
