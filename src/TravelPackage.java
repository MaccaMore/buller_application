import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

public class TravelPackage implements Serializable {
    // Core package
    // We use this to reset next ID when loading travelpackages
    public static final int STARTING_ID = 100;
    private static int next_id = STARTING_ID;
    private final int packageID;
    private Customer customer;
    private Accommodation accommodation;
    private String start_date;
    private int stay_duration;

    private Double total_price;

    // Additions to package
    // Maybe these should be split into POJO called products?
    private int lessons = 0;
    private boolean season_pass = false;
    private int lift_pass_days = 0;

    // Constants for easy change of prices. Constants can be made public safely because they can't be messed with.
    // I read this is good practice as magic numbers are BAD?
    public static final Double SEASON_PASS_COST = 200.0;
    public static final int SEASON_PASS_DAYS = 99;
    public static final Double DAILY_PASS_COST = 26.0;
    public static final Double DISCOUNT_PERCENTAGE = 10.0;
    // Look could honestly just use a HashMap here. Lesson Costs are for Beginner, Intermediate, Expert
    public static final List<Double> LESSON_COST = Arrays.asList(25.0, 20.0, 15.0);

    public TravelPackage(Customer customer, String start_date, int stay_duration, Accommodation accommodation) {
        this.packageID = next_id++;
        this.customer = customer;
        this.start_date = start_date;
        this.stay_duration = stay_duration;
        this.accommodation = accommodation;
        setTotalPrice();
    }

    // Because the functions to calculate price are within this class, we make a dummy method so that nextID is not incremented.
    public TravelPackage(Customer customer, String start_date, int stay_duration, Accommodation accommodation, boolean dummy){
        this.customer = customer;
        this.start_date = start_date;
        this.stay_duration = stay_duration;
        this.accommodation = accommodation;
        this.packageID = 0;
        setTotalPrice();
    }

    public int getPackageID() {
        return packageID;
    }

    // Used to set available accomm to unavailable when loaded
    public int getAccomId(){
        return accommodation.getId();
    }

    public String getAccommAddress(){
       return accommodation.getFullAddress();
    }

    public int getCustomerID(){
        return customer.getId();
    }

    public String getCustomerSkill(){
        return customer.getSkillLevel();
    }
    public String getCustomerName(){
        return customer.getName();
    }
    public Customer getCustomerObject() {
        return customer;
    }

    public int getLessons(){
        return lessons;
    }

    public boolean getSeasonPass(){
        return season_pass;
    }

    public Double getTotalPrice(){return total_price;}

    public String getStartDate(){
        return start_date;
    }

    public int getStayDuration(){
        return stay_duration;
    }

    public int getLiftPassDays(){
        return lift_pass_days;
    }

    // Variables like cost are subject to change frequently.
    public Double getLiftPassCost(int days){
        double base_cost = days * DAILY_PASS_COST;
        // discount cost is daily pass cost - the discount percentage
        double discount_cost = base_cost - (base_cost * (DISCOUNT_PERCENTAGE/100.0));
        if (days < 5){
            return base_cost;
        } else if (discount_cost < SEASON_PASS_COST) {
            return discount_cost;
        } else {
            return SEASON_PASS_COST;
        }
    }

    public Double getLessonCost(){
        if (customer.getSkillLevel().equals("Beginner")){
        return LESSON_COST.get(0);
    } else if (customer.getSkillLevel().equals("Intermediate")){
        return LESSON_COST.get(1);
    } else {
        return LESSON_COST.get(2);
    }
    }
    public Double getTotalLessonCost(int lessons){
        if (customer.getSkillLevel().equals("Beginner")){
            return lessons*LESSON_COST.get(0);
        } else if (customer.getSkillLevel().equals("Intermediate")){
            return lessons*LESSON_COST.get(1);
        } else {
            return lessons*LESSON_COST.get(2);
        }
    }

  public static void setNextID(int ID){
        next_id = ID + 1;
  }

    // Because customer can change, we need to ensure that the package is updated.
    public void setCustomer(Customer customer){
        this.customer = customer;
    }

    public void setLiftPassDays(int lift_pass_days){
        // We set true or false for lift pass, allows user to change lift pass days
        // If the function returns the price of the season pass they will get a season pass
        this.season_pass = getLiftPassCost(lift_pass_days) >= SEASON_PASS_COST;
        if (season_pass){
            this.lift_pass_days = 99;
        } else {
            this.lift_pass_days = lift_pass_days;
        }
        setTotalPrice();
    }

    public void setLessons(int lessons){
            this.lessons = lessons;
            setTotalPrice();
    }

    public void setTotalPrice(){
        this.total_price = this.getLiftPassCost(lift_pass_days) +
                (this.accommodation.getPricePerNight()*this.stay_duration) +
                this.getTotalLessonCost(lessons);
    }

    public int daysForPassValue(){
        double days = SEASON_PASS_COST / (DAILY_PASS_COST - (DAILY_PASS_COST * (DISCOUNT_PERCENTAGE) / 100));
        return (int) days + 1;
    }

    public String toString() {
        return "Package ID: " + packageID +
                "\n Customer: " + customer.getName() + getCustomerID() +
                "\n Start date: " + start_date +
                "\n Days stayed: " + stay_duration +
                "\n Accommodation: " + accommodation.getFullAddress() +
                "\n Lessons: " + lessons +
                "\n Lift pass days: " + lift_pass_days +
                "\n Season Pass: " + season_pass +
                "\n Total Price: $" + total_price + " \n";
    }
}
