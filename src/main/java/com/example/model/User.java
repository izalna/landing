package com.example.model;

public class User {
    private int id;
    private String email;
    private String username;
    private String password;
    private Name name;
    private Address address;
    private String phone;
    // Tidak ada role, karena semua user memiliki akses sama setelah login

    // Inner class for Name
    public static class Name {
        private String firstname;
        private String lastname;

        public String getFirstname() {
            return firstname;
        }

        public void setFirstname(String firstname) {
            this.firstname = firstname;
        }

        public String getLastname() {
            return lastname;
        }

        public void setLastname(String lastname) {
            this.lastname = lastname;
        }

        @Override
        public String toString() {
            return firstname + " " + lastname;
        }
    }

    // Inner class for Address
    public static class Address {
        private String city;
        private String street;
        private int number;
        private String zipcode;
        private Geolocation geolocation;

        public static class Geolocation {
            private String lat;
            private String longi; // 'long' is a reserved keyword, use 'longi'

            public String getLat() {
                return lat;
            }

            public void setLat(String lat) {
                this.lat = lat;
            }

            public String getLongi() {
                return longi;
            }

            public void setLongi(String longi) {
                this.longi = longi;
            }
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getStreet() {
            return street;
        }

        public void setStreet(String street) {
            this.street = street;
        }

        public int getNumber() {
            return number;
        }

        public void setNumber(int number) {
            this.number = number;
        }

        public String getZipcode() {
            return zipcode;
        }

        public void setZipcode(String zipcode) {
            this.zipcode = zipcode;
        }

        public Geolocation getGeolocation() {
            return geolocation;
        }

        public void setGeolocation(Geolocation geolocation) {
            this.geolocation = geolocation;
        }

        @Override
        public String toString() {
            return number + " " + street + ", " + city + " " + zipcode;
        }
    }

    // Getters and Setters for User
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Name getName() {
        return name;
    }

    public void setName(Name name) {
        this.name = name;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Override
    public String toString() {
        // Handle null name gracefully
        return username + " (" + (name != null ? name.toString() : "N/A") + ")";
    }
}