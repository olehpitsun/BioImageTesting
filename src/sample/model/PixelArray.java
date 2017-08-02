package sample.model;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.highgui.Highgui;
import sample.BranchPoints;
import sample.EndPoints;
import sample.PointWeight;
import sample.SkeletonPoints;
import sample.controller.StartController;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import static sample.controller.StartController.img_name;

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

    public double[][] getPicdata(){
        return picdata;
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

    public double finalLinesWeight = 0.0;
    public double finalLinesAreaWeight = 0.0;
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_CYAN = "\u001B[36m";

    /**
     * рахує результати зважування
     * @param branchPointses - точки гілок (початок - кінець, вага)
     */
    public void calculateResult (List<BranchPoints> branchPointses){


        for (int i = 0 ; i < branchPointses.size(); i++){

            double weight = calculateLineWeight(branchPointses.get(i).getX_1(), branchPointses.get(i).getY_1(),
                    branchPointses.get(i).getX_2(), branchPointses.get(i).getY_2());

            finalLinesWeight += weight;
            finalLinesAreaWeight += branchPointses.get(i).getArea();

        }

        showResultsToConsole(branchPointses);
    }

    private void showResultsToConsole(List<BranchPoints> branchPointses){


        for (int i = 0 ; i < branchPointses.size(); i++){

            double weight = calculateLineWeight(branchPointses.get(i).getX_1(), branchPointses.get(i).getY_1(),
                    branchPointses.get(i).getX_2(), branchPointses.get(i).getY_2());

            if(weight != 0 ){
                System.out.println(ANSI_CYAN + branchPointses.get(i).getY_1() + " | " + branchPointses.get(i).getX_1() + " => " +
                        branchPointses.get(i).getY_2() + " | " + branchPointses.get(i).getX_2() + ANSI_RESET + " Вага гілки: " + weight +
                        " |||||| " + ANSI_GREEN + " Відношення = " + new BigDecimal(weight/finalLinesWeight).setScale(2, RoundingMode.HALF_UP).floatValue() +
                        " % " + ANSI_RESET +"|||||| Різниця площа полігону без гілки: " + branchPointses.get(i).getArea() + "" +
                        "  .......... " + ANSI_PURPLE + new BigDecimal((branchPointses.get(i).getArea())/finalLinesAreaWeight).setScale(2, RoundingMode.HALF_UP).floatValue()  + "%" + ANSI_RESET);
            }

        }
    }

    private double drawNewPolygon(double[][] temp_picdata, List <PointWeight> points_weights)  {
        String src_path = "C:\\data\\BioImageTesting1\\src\\sample\\image\\blank.png";

        Mat image1 = Highgui.imread(src_path);

        for(int h=0;h<points_weights.size();h++) {
            int x =points_weights.get(h).y;
            int y =points_weights.get(h).x;
            int d = points_weights.get(h).distance;
            if(temp_picdata[y][x] < 100){
                Core.rectangle(image1, new Point(x, y), new Point(x + d, y + d ), new Scalar(0, 0, 0), Core.FILLED);
                Highgui.imwrite("C:\\data\\BioImageTesting1\\src\\sample\\image\\"+img_name+"\\result_" + line.get(0).x + " _ " + line.get(0).y + ".png", image1);
            }
        }

        StartController startController = new StartController();
        System.out.println(line.get(0).x + " _ " + line.get(0).y + line.get(line.size()-1).x + " _ " + line.get(line.size()-1).y + ".png" + " = " );
        //System.out.println(StartController.fullPolygonArea - startController.calculateArea(image1));
        System.out.println((StartController.fullPolygonArea - startController.calculateArea(image1)) / StartController.fullPolygonArea);

        return StartController.fullPolygonArea - startController.calculateArea(image1);


    }

    private double drawPolygonForInnerLines(double[][] temp_picdata, List <PointWeight> points_weights, int x1, int y1, int x2, int y2 ){
        String src_path = "C:\\data\\BioImageTesting1\\src\\sample\\image\\blank.png";

        Mat image1 = Highgui.imread(src_path);

        int temp_X=0; int temp_Y=0;
        if(x1>x2){
            temp_X = x1; x1 = x2; x2 = temp_X;
            temp_Y = y1; y1 = y2; y2 = temp_Y;


        }



        for(int i = 0; i < points_weights.size(); i++){

            if(points_weights.get(i).y > x1 && points_weights.get(i).y < x2 && points_weights.get(i).x < y1+20 && points_weights.get(i).x > y1-20){

                //System.out.println(points_weights.get(i).y + " :::: ");
                temp_picdata[points_weights.get(i).x][points_weights.get(i).y] = 255.0;
            }
        }



        for(int h=0;h<points_weights.size();h++) {
            int x =points_weights.get(h).y;
            int y =points_weights.get(h).x;
            int d = points_weights.get(h).distance;
            if(temp_picdata[y][x] < 100){
                Core.rectangle(image1, new Point(x, y), new Point(x + d, y + d ), new Scalar(0, 0, 0), Core.FILLED);
                Highgui.imwrite("C:\\data\\BioImageTesting1\\src\\sample\\image\\"+img_name+"\\result_" + x1 + " _ " + y1 + ".png", image1);
            }
        }

        StartController startController = new StartController();
        System.out.println(x1 + " _ " + y1 + " | " + x2 + " _ " + y2 + ".png");
        //System.out.println(StartController.fullPolygonArea - startController.calculateArea(image1));
        System.out.println((StartController.fullPolygonArea - startController.calculateArea(image1)) / StartController.fullPolygonArea);

        for(int i = 0; i < points_weights.size(); i++){
            if(points_weights.get(i).y > x1 && points_weights.get(i).y < x2 ){
                temp_picdata[points_weights.get(i).x][points_weights.get(i).y] = 0.0;
            }
        }


        return StartController.fullPolygonArea - startController.calculateArea(image1);
    }

    /**
     * Повертає вагу кожної лінії (сума ваг точок)
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @return -вага лінії
     */
    private double calculateLineWeight(int x1, int y1, int x2, int y2){

        List<SkeletonPoints> linePoints = this.pixelsOfLines(x1,y1,x2,y2);
        double weightOfLine = 0;

        try{
            for(int i = 0 ; i < linePoints.size(); i+=5){
                weightOfLine += 5*getWeight(linePoints.get(i).x, linePoints.get(i).y);
            }
        }catch (Exception e){}

        return weightOfLine;
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
                    if((picdata[temp_i-1][temp_j] < 100 && picdata[temp_i+1][temp_j] < 100
                            && (picdata[temp_i][temp_j-1] < 100 || picdata[temp_i][temp_j+1] < 100))
                            ||
                            (picdata[temp_i][temp_j-1] < 100 && picdata[temp_i][temp_j+1] < 100
                                    && (picdata[temp_i-1][temp_j] < 100 || picdata[temp_i+1][temp_j] < 100))

                            ){
                        //
                        //if(picdata[temp_i][temp_j+1] < 100 && picdata[temp_i-1][temp_j] < 100 ||
                        //       picdata[temp_i][temp_j-1] < 100 && picdata[temp_i-1][temp_j] < 100){

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

    public List<SkeletonPoints> line = new ArrayList<SkeletonPoints>();
    public List<EndPoints> innerPoints = new ArrayList<EndPoints>();

    private boolean isInnerPoint(int X, int Y){

        //System.out.println(innerPoints.size());
        boolean isInnerPoint = false;
        for(int i = 0; i < innerPoints.size(); i++){
            //System.out.println(innerPoints.get(i).x +"= " +  Y + " = " + innerPoints.get(i).y + " +" + X);
            if(innerPoints.get(i).x == X && innerPoints.get(i).y == Y){
                isInnerPoint = true;
            }
        }

        return isInnerPoint;
    }

    private boolean isBeforePoint(double[][] picdata){
        boolean isB = true;

        for(int i = 0; i < picdata.length;i++){
            for(int j = 0 ; j < picdata.length; j++){
                if(picdata[i][0] == line.get(line.size()-1).x && picdata[i][0] == line.get(line.size()-1).x){

                }
            }
        }

        return isB;
    }



    public void findPointsOfLine(int X, int Y, int count){

        System.out.println(isInnerPoint(X, Y));
        if(!isInnerPoint(X, Y) ){
            if(count < 70) {
                int pointer_X = X;
                int pointer_Y = Y;

                if (picdata[X][Y - 1] < 100) {
                    pointer_X = X;
                    pointer_Y = Y - 1;
                } else if (picdata[X + 1][Y - 1] < 100) {
                    pointer_X = X + 1;
                    pointer_Y = Y - 1;
                } else if (picdata[X + 1][Y] < 100) {
                    pointer_X = X + 1;
                    pointer_Y = Y;
                } else if (picdata[X + 1][Y + 1] < 100) {
                    pointer_X = X + 1;
                    pointer_Y = Y + 1;
                } else if (picdata[X][Y + 1] < 100) {
                    pointer_X = X;
                    pointer_Y = Y + 1;
                } else if (picdata[X - 1][Y + 1] < 100) {
                    pointer_X = X - 1;
                    pointer_Y = Y + 1;
                } else if (picdata[X - 1][Y - 1] < 100) {
                    pointer_X = X - 1;
                    pointer_Y = Y - 1;
                }

                System.out.println("rec " + pointer_X + " ;;;;;; " + pointer_Y);
                line.add(new SkeletonPoints(pointer_X, pointer_Y));

                count = count+1;
                findPointsOfLine(pointer_X, pointer_Y, count);
            }
        }
    }

    public void findPointsOfLowerLine(int X, int Y, int count){

        System.out.println(isInnerPoint(X, Y));

       // System.out.println(X + " __ " + Y + " +++++++++++ " + X+1 + "  :::: " + (Y-1) + " ' ");
        if(!isInnerPoint(X, Y) ){
            if(count < 70) {
                int pointer_X = X;
                int pointer_Y = Y;

                if (picdata[X - 1][Y] < 100) {
                    pointer_X = X - 1;
                    pointer_Y = Y;
                } else if (picdata[X - 1][Y - 1] < 100) {
                    pointer_X = X - 1;
                    pointer_Y = Y - 1;
                } else if (picdata[X][Y - 1] < 100) {
                    pointer_X = X;
                    pointer_Y = Y - 1;
                } else if (picdata[X + 1][Y - 1] < 100) {

                    pointer_X = X + 1;
                    pointer_Y = Y - 1;
                } else if (picdata[X + 1][Y] < 100) {
                    pointer_X = X + 1;
                    pointer_Y = Y;
                } else if (picdata[X + 1][Y + 1] < 100) {
                    pointer_X = X + 1;
                    pointer_Y = Y + 1;
                } else if (picdata[X][Y + 1] < 100) {
                    pointer_X = X;
                    pointer_Y = Y + 1;
                } else if (picdata[X - 1][Y + 1] < 100) {
                    pointer_X = X - 1;
                    pointer_Y = Y + 1;
                } else if (picdata[X - 1][Y - 1] < 100) {
                    pointer_X = X - 1;
                    pointer_Y = Y - 1;
                }

                System.out.println("lower rec " + pointer_X + " ;;;;;; " + pointer_Y);
                line.add(new SkeletonPoints(pointer_X, pointer_Y));
                count = count+1;
                findPointsOfLowerLine(pointer_X, pointer_Y, count);
            }
        }
    }

    public void findPointsOfLowerLeftLine(int X, int Y, int count){
        System.out.println("left");
        System.out.println(isInnerPoint(X, Y));

        // System.out.println(X + " __ " + Y + " +++++++++++ " + X+1 + "  :::: " + (Y-1) + " ' ");
        if(!isInnerPoint(X, Y) ){

            if(count < 70) {
                int pointer_X = X;
                int pointer_Y = Y;


                if (picdata[X][Y + 1] < 100) {
                    pointer_X = X;
                    pointer_Y = Y + 1;
                } else if (picdata[X + 1][Y] < 100) {
                    pointer_X = X + 1;
                    pointer_Y = Y;
                } else if (picdata[X + 1][Y + 1] < 100) {
                    pointer_X = X + 1;
                    pointer_Y = Y + 1;
                } else if (picdata[X][Y + 1] < 100) {
                    pointer_X = X;
                    pointer_Y = Y + 1;
                } else if (picdata[X - 1][Y + 1] < 100) {
                    pointer_X = X - 1;
                    pointer_Y = Y + 1;
                } else if (picdata[X - 1][Y - 1] < 100) {
                    pointer_X = X - 1;
                    pointer_Y = Y - 1;
                }

                System.out.println("lower rec " + pointer_X + " ;;;;;; " + pointer_Y);
                line.add(new SkeletonPoints(pointer_X, pointer_Y));
                count = count+1;
                findPointsOfLowerLeftLine(pointer_X, pointer_Y, count);
            }
        }
    }

    public int count=0;

    public List<BranchPoints> estimateWeightOfSkeleton(List<EndPoints> innerPoints1, List<EndPoints> endPoints, List <PointWeight> points_weights){


        System.out.println("estimateWeightOfSkeleton");
        innerPoints = innerPoints1;
        List<BranchPoints> branchPointses = new ArrayList<BranchPoints>();


        double teemp_picdata[][] = new double[174][120];


        for(int i = 0; i < endPoints.size(); i++){

            System.arraycopy(picdata, 0, teemp_picdata, 0, picdata.length);

            if(endPoints.get(i).x < innerPoints.get(innerPoints.size()-1).x){

                line.add(new SkeletonPoints(endPoints.get(i).x, endPoints.get(i).y));
                //try {

               count =0;
                findPointsOfLine(endPoints.get(i).x, endPoints.get(i).y, count);
                // }catch (Exception e){}
                if(count>29){
                    System.out.println("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n");
                }

                System.out.println(" End_____________________________________");
            }else {

                if(endPoints.get(i).y < innerPoints.get(innerPoints.size()-1).y){
                    System.out.println("lllll");
                    line.add(new SkeletonPoints(endPoints.get(i).x, endPoints.get(i).y));
                    //try {
                    System.out.println(endPoints.get(i).x + " ;;;; " +  endPoints.get(i).y);

                   count=0;
                    findPointsOfLowerLeftLine(endPoints.get(i).x, endPoints.get(i).y, count);

                    if(count>29){
                        System.out.println("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n");
                    }
                    // }catch (Exception e){}
                    System.out.println(" End_____________________________________");
                }else{
                    System.out.println("lllll");
                    line.add(new SkeletonPoints(endPoints.get(i).x, endPoints.get(i).y));
                    //try {
                    System.out.println(endPoints.get(i).x + " ;;;; " +  endPoints.get(i).y);

                     count = 0;
                    findPointsOfLowerLine(endPoints.get(i).x, endPoints.get(i).y, count);
                    if(count>29){
                        System.out.println("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n");
                    }
                    // }catch (Exception e){}
                    System.out.println(" End_____________________________________");
                }
            }



            /*** малюємо точки, що належать гілці в білий*/
            for(int h=0; h < line.size(); h++ ){
                teemp_picdata[line.get(h).x][line.get(h).y] = 255.0;
            }


            double areaWeight = 0.0;
            try {
                areaWeight = drawNewPolygon(teemp_picdata,points_weights );
            } catch (Exception e) {
                e.printStackTrace();
            }

            branchPointses.add(new BranchPoints(line.get(0).x, line.get(0).y, line.get(line.size()-1).x, line.get(line.size()-1).y , areaWeight));


            /*** малюємо точки, що належать гілці в чорний*/
            for(int h=0; h < line.size(); h++ ){
                teemp_picdata[line.get(h).x][line.get(h).y] = 0.0;
            }

            // очищаємо список точок гілки
            line.clear();

        }

        try {
            int j=0;
            double inner_area = 0.0;
            for(int i = 0; i < endPoints.size(); i++) {
                j = i + 1;
                try {
                    inner_area = drawPolygonForInnerLines(teemp_picdata,points_weights, innerPoints.get(i).y, innerPoints.get(i).x, innerPoints.get(j).y, innerPoints.get(j).x );
                } catch (Exception e) {
                    e.printStackTrace();
                }

                branchPointses.add(new BranchPoints(innerPoints.get(i).y, innerPoints.get(i).x, innerPoints.get(j).y, innerPoints.get(j).x, inner_area));

            }
        }catch (Exception e){}




        /*
        // пошук крайнього мін значення (внутрішньої точки)
        int min_right_X = innerPoints.get(0).y; int min_right_Y=innerPoints.get(0).x;
        for(int i=0; i < innerPoints.size(); i++){
            if(innerPoints.get(i).y < min_right_X){
                min_right_X = innerPoints.get(i).y; min_right_Y= innerPoints.get(i).x;
            }
        }
        System.out.println("left ********************************************************");
        for(int i = 0; i < endPoints.size(); i++){
            if(endPoints.get(i).y < min_right_X){

                branchPointses.add(new BranchPoints(min_right_X, min_right_Y, endPoints.get(i).y, endPoints.get(i).x));

                /*
                System.out.println(min_right_X + " | " + min_right_Y + "   =>    " + endPoints.get(i).y + " | " + endPoints.get(i).x );



                System.out.println(this.EuclidianDistance(min_right_X, min_right_Y, endPoints.get(i).y, endPoints.get(i).x )*
                        (
                                sumOFWeight(points_weights, min_right_X,min_right_Y ) +
                                        sumOFWeight(points_weights, endPoints.get(i).y,endPoints.get(i).x )
                        ));
                *//*
            }
        }*/
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
/*
        // між внутрішніми точками (вузлами)
        System.out.println("-----------------------------------------------");

        try {
            int j = 0;
            for(int i = 0; i < endPoints.size(); i++){
                j=i+1;

                branchPointses.add(new BranchPoints(innerPoints.get(i).y, innerPoints.get(i).x, innerPoints.get(j).y, innerPoints.get(j).x));
                /*
                System.out.println(innerPoints.get(i).y + " | " + innerPoints.get(i).x + "   =>   " + innerPoints.get(j).y + " | " + innerPoints.get(j).x );
                System.out.println(this.EuclidianDistance(innerPoints.get(i).y, innerPoints.get(i).x,
                        innerPoints.get(j).y, innerPoints.get(j).x )*(
                        sumOFWeight(points_weights, innerPoints.get(j).y,innerPoints.get(j).x ) +
                                sumOFWeight(points_weights, innerPoints.get(i).y,innerPoints.get(i).x )
                ));
                */
/*
                //якщо між 2 внутрішніми точками є гілки
                for(int u = 0; u < endPoints.size(); u++){
                    if(endPoints.get(u).y > innerPoints.get(i).y && endPoints.get(u).y < innerPoints.get(j).y){
                        branchPointses.add(new BranchPoints( endPoints.get(u).y , endPoints.get(u).x, innerPoints.get(i+1).y, innerPoints.get(i+1).x));



                        /*
                        System.out.println("+++++++++++++++++++++++++++++++++");
                        System.out.println(innerPoints.get(i).y + " | " + innerPoints.get(i).x + "   =>   " +
                                endPoints.get(u).y + " | " + endPoints.get(u).x);

                        System.out.println(this.EuclidianDistance(innerPoints.get(i).y, innerPoints.get(i).x,
                                endPoints.get(u).y, endPoints.get(u).x )*(
                                sumOFWeight(points_weights, innerPoints.get(i).y,innerPoints.get(i).x ) +
                                        sumOFWeight(points_weights, endPoints.get(u).y,endPoints.get(u).x )
                        ));
                        System.out.println("+++++++++++++++++++++++++++++++++");
                        *//*
                    }
                }

            }
        }catch (Exception e){}



        // пошук макс правого вузла
        int max_right_X = innerPoints.get(0).y; int max_right_Y=innerPoints.get(0).x;
        for(int i=0; i < innerPoints.size(); i++){
            if(innerPoints.get(i).y > max_right_X){
                max_right_X = innerPoints.get(i).y; max_right_Y= innerPoints.get(i).x;
            }
        }

        System.out.println("right ********************************************************");
        for(int i = 0; i < endPoints.size(); i++){
            if(endPoints.get(i).y > max_right_X){

                branchPointses.add(new BranchPoints(max_right_X, max_right_Y, endPoints.get(i).y , endPoints.get(i).x));
                /*
                System.out.println(max_right_X + " | " + max_right_Y + "   =>   " + endPoints.get(i).y + " | " +  endPoints.get(i).x);

                //this.pixelsOfLines(max_right_X, max_right_Y, endPoints.get(i).y, endPoints.get(i).x);

                System.out.println(this.EuclidianDistance(max_right_X, max_right_Y, endPoints.get(i).y, endPoints.get(i).x )*
                        (
                        sumOFWeight(points_weights, max_right_X,max_right_Y ) +
                                sumOFWeight(points_weights, endPoints.get(i).y,endPoints.get(i).x )
                ));
                *//*
            }
        }

        */
        return branchPointses;
    }






    private List<SkeletonPoints> pixelsOfLines(int a, int b, int c, int d){

        List<SkeletonPoints> linePoints = new ArrayList<SkeletonPoints>();
        // calculate distance between the two points
        double DT = Math.sqrt(Math.pow((c - a), 2) + Math.pow((d - b), 2));

        double D = 1.0; // distance to point C

        for(int i=1; i < DT; i++){

            double T = i / DT;

            // finding point C coordinate
            double x = (1 - T) * a + T * c;
            double y = (1 - T) * b + T * d;

            linePoints.add(new SkeletonPoints((int)x,(int)y));
            //System.out.println("Point C coordinates:\n  x: " + x + "\n  y: " + y);

        }
        return linePoints;
    }

    private double EuclidianDistance(int x1, int y1, int x2, int y2){

        return Math.sqrt( Math.pow((x2-x1),2) + Math.pow((y2-y1),2));

    }


    /**
     * повертає вагу 1 точки на лінії
     * @param x
     * @param y
     * @return
     */
    public double getWeight(int x , int y){

        int top_count = 0;
        int bottom_count = 0;
        int righr_count = 0;
        int left_count = 0;

        List <PointWeight> points_weight = new ArrayList<PointWeight>();



        //for(int i = 0; i < sk_points.size(); i++){
        List<Integer> sides = new ArrayList<Integer>();

        top_count = this.topSide(x, y);
        bottom_count = this.bottomSide(x, y);
        righr_count = this.rightSide(x, y);
        left_count = this.leftSide(x, y);

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
        for(int h=1; h<sides.size(); h++){
            if(sides.get(h) < min){
                min = sides.get(h);
            }
        }

        //points_weight.add(new PointWeight(sk_points.get(i).x, sk_points.get(i).y, min));
        /// }
        return min;
    }


    public List<PointWeight> getFullSkeletWeight(List <SkeletonPoints> sk_points){

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




    public int topSide(int X, int Y){
        int dist=0;

        try {
            for(int i=0; i<100; i++){

                if(StartController.picdata[X--][Y] < 100){
                    dist++;
                }
            }
        }catch (Exception e){
            //System.out.println(e);
        }
        return dist;
    }

    public int bottomSide(int X, int Y){
        int dist=0;

        try {
            for(int i=0; i<100; i++){

                if(StartController.picdata[X++][Y] < 100){
                    dist++;
                }
            }
        }catch (Exception e){
            //System.out.println(e);
        }
        return dist;
    }

    public int rightSide(int X, int Y){
        int dist=0;

        try {
            for(int i=0; i<100; i++){

                if(StartController.picdata[X][Y++] < 100){
                    dist++;
                }
            }
        }catch (Exception e){
            //System.out.println("right s" + e);
        }
        return dist;
    }

    public int leftSide(int X, int Y){
        int dist=0;

        try {
            for(int i=0; i<100; i++){

                if(StartController.picdata[X][Y--] < 100){
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


    /**
     private void calculateTopPoint(List<SkeletonPoints> linePoints, List<SkeletonPoints>sk_points){


     List<PointWeight> min_distance = new ArrayList<PointWeight>();

     double min = this.EuclidianDistance(linePoints.get(0).x, linePoints.get(0).y, sk_points.get(0).x, sk_points.get(0).y) ;
     int x = linePoints.get(0).x;
     int y = linePoints.get(0).y;

     for(int i = 0; i < linePoints.size(); i++){
     for(int j = 0; j < sk_points.size(); j++){
     double temp_min = this.EuclidianDistance(linePoints.get(i).x, linePoints.get(i).y, sk_points.get(j).x, sk_points.get(j).y);
     if(temp_min < min){
     min = temp_min;
     x = linePoints.get(j).x;
     y = linePoints.get(j).y;
     //System.out.println(x + ";"+y + ";"+ min);

     }
     }

     min_distance.add(new PointWeight(x,y,(int)min));
     }


     this.getMaxFromMin(min_distance);
     min_distance.clear();



     }

     private void getMaxFromMin(List<PointWeight> min_distance){
     int max = min_distance.get(0).distance;
     int x_new = min_distance.get(0).x;
     int y_new = min_distance.get(0).y;


     for(int i = 0; i < min_distance.size(); i++){
     if( min_distance.get(i).distance > max){
     max = min_distance.get(i).distance;
     x_new = min_distance.get(i).x;
     y_new = min_distance.get(i).y;
     }
     }
     System.out.println("Max " + x_new + " | " + y_new + " = " + max);
     }
     **/
}
