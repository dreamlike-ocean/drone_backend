package com.example.starter.MobileDevice.DeviceMsg;



public abstract class CoordinatePoint {
    private int degree;
    private int minutes;
    private char mark;

    public CoordinatePoint(int degree, int minutes, char mark) {
        this.degree = degree;
        this.minutes = minutes;
        this.mark = mark;
    }

    public static class Latitude extends CoordinatePoint{
        /**
         *
         * @param degree dd 倍率依次为*10 *1
         * @param minutes mmmmmm 倍率依次为X10 X1 X0.1 X0.01 X0.001 X0.0001 共6个
         * @param mark N or S 北南纬
         */
        public Latitude(int degree, int minutes, char mark) {
            super(degree, minutes, mark);
        }
        //不是我有意这么写的
        //java规定必须第一行写关于super初始化
        public Latitude(String cmd){
            this(Integer.parseInt(cmd.substring(0, 2)),Integer.parseInt(cmd.substring(2, 8)),cmd.charAt(cmd.length()-1));
        }
        public boolean isNorth(){
            return super.mark == 'N';
        }
        public boolean isSouth(){
            return !isNorth();
        }
    }

    public static class Longitude extends CoordinatePoint{
        /**
         *
         * @param degree ddd 倍率为 *100 *10 *1
         * @param minutes mmmmmm 倍率依次为X10 X1 X0.1 X0.01 X0.001 X0.0001 共6个
         * @param mark E OR W 东西经
         */
        public Longitude(int degree, int minutes, char mark) {
            super(degree, minutes, mark);
        }

        public Longitude(String cmd){
            this(Integer.parseInt(cmd.substring(0, 3)),Integer.parseInt(cmd.substring(3, 9)),cmd.charAt(cmd.length()-1));
        }

        public boolean isEast(){
            return super.mark == 'E';
        }
        public boolean isWest(){
            return !isEast();
        }
    }


    public int getDegree() {
        return degree;
    }

    public void setDegree(int degree) {
        this.degree = degree;
    }

    /**
     *
     * @return mmmmmm 倍率依次为X10 X1 X0.1 X0.01 X0.001 X0.0001 共6个
     */
    public int getMinutes() {
        return minutes;
    }

    public void setMinutes(int minutes) {
        this.minutes = minutes;
    }

    public char getMark() {
        return mark;
    }

    public void setMark(char mark) {
        this.mark = mark;
    }
}
