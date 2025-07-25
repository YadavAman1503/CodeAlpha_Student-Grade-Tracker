public class Student {
    private int id;
    private String name;
    private int english, hindi, marathi, maths, science, socialScience, geography;

    public Student(int id, String name, int english, int hindi, int marathi, int maths, int science, int social, int geography) {
        this.id = id;
        this.name = name;
        this.english = english;
        this.hindi = hindi;
        this.marathi = marathi;
        this.maths = maths;
        this.science = science;
        this.socialScience = social;
        this.geography = geography;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public int getEnglish() { return english; }
    public int getHindi() { return hindi; }
    public int getMarathi() { return marathi; }
    public int getMaths() { return maths; }
    public int getScience() { return science; }
    public int getSocialScience() { return socialScience; }
    public int getGeography() { return geography; }

    public int getTotalScore() {
        return english + hindi + marathi + maths + science + socialScience + geography;
    }

    public double getAverageScore() {
        return getTotalScore() / 7.0;
    }
}
