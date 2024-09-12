import java.io.Serializable;

public class Accommodation implements Serializable {
    private static int next_id = 100;
    private Integer id;
    private String type;
    private String address;
    private String city;
    private String post_code;
    private Double price_per_night;
    private boolean available;

    public Accommodation(String type, String address, String city,String post_code , Double price_per_night, boolean available) {
        this.id = next_id++;
        this.type = type;
        this.address = address;
        this.city = city;
        this.post_code = post_code;
        this.price_per_night = price_per_night;
        this.available = available;
    }

    public boolean isAvailable() {
        return available;
    }

    public Integer getId() {
        return id;
    }

    public String getType(){
        return type;
    }

    public String getAddress() {
        return address;
    }
    public String getCity(){
        return city;
    }

    public String getPostCode(){
        return post_code;
    }

    public String getFullAddress(){
        return address + " " + city + " " + post_code;
    }

    public Double getPricePerNight() {
        return price_per_night;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public String toString() {
       return  id + " : " + address;
    }
}

