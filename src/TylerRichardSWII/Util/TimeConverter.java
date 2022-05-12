package TylerRichardSWII.Util;

import TylerRichardSWII.AllClass.Appointment;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;

import java.time.*;
import java.time.format.DateTimeFormatter;

import static java.time.ZoneId.systemDefault;

/**
 * This class handles most of the time management aspects of this application minus a few in line formattings
 */
public class TimeConverter {
    private  DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss z");
    private String time;
    private LocalDateTime timeDefault;
    private String date;
    private final Alert errorMessage = new Alert(Alert.AlertType.WARNING);



    /**
     * @param date - date to be added to time to convert to utc
     * @param time - time to be added with date to convert to utc
     * @return returns the inputted time as UTC
     */
    public ZonedDateTime convertToUTC(LocalDate date, LocalTime time) {
        ZonedDateTime localToZone = ZonedDateTime.of(date, time, systemDefault());
        ZonedDateTime utc = localToZone.withZoneSameInstant(ZoneId.of("UTC"));
        return utc;


    }

    /**
     * @return this gets the local time and returns it
     */
    public ZonedDateTime getLocalTimeNow() {
        return ZonedDateTime.now(ZoneId.systemDefault());
    }

    /**
     * @return returns the current time in UTC
     */
    public String convertToUTC() {
        ZonedDateTime timeDefault = ZonedDateTime.now(Clock.systemUTC()).withZoneSameInstant(ZoneId.of(("UTC")));
        return timeDefault.format(formatter);
    }


    /**
     * @param time takes in a local time and date
     * @return returns a local time and date formatted
     */
    public String formatTime(LocalDateTime time) {
        return time.format(formatter);
    }


    /**
     * @param dateTime takes in a localDateTime
     * @return ZonedDateTime within the current time zone
     */
    public ZonedDateTime convertToLocalTime(LocalDateTime dateTime) {
        ZoneId utc = ZoneId.of("UTC");
        ZonedDateTime utcTime = dateTime.atZone(utc);
        ZoneId localZone = ZoneId.systemDefault();
        ZonedDateTime toLocal = utcTime.withZoneSameInstant(localZone);
        return toLocal;
    }


    /**
     * @return returns default time zone
     */
    public String getTimeZone() {
        ZoneId systemClock = Clock.systemDefaultZone().getZone();
        String timeZone = systemClock.toString();
        return timeZone;
    }


    /**
     * @param utcStartDateTime - start time of appointment
     * @param utcEndDateTime - end time of appointment
     * @param allAppointments - list of all appointments
     * @return T/F based off of already existing appointments
     */
    public boolean checkBusinessHours(ZonedDateTime utcStartDateTime, ZonedDateTime utcEndDateTime,
                                      ObservableList<Appointment> allAppointments) {

       //booleans are used to check first that the time submitted is within business hours and if it is it then checks
        //for conflicting appointments.
        boolean businessHourCheck;
        boolean conflictingAppointments = true;
        LocalDate submittedStartDate = utcStartDateTime.toLocalDate();
        ZonedDateTime openHour = ZonedDateTime.of(submittedStartDate, LocalTime.of(7, 59), ZoneId.of("US/Eastern"));
        ZonedDateTime openHourUTC = openHour.withZoneSameInstant(ZoneId.of("UTC"));
        ZonedDateTime closeHour = ZonedDateTime.of(submittedStartDate, LocalTime.of(22, 01), ZoneId.of("US/Eastern"));
        ZonedDateTime closeHourUTC = closeHour.withZoneSameInstant(ZoneId.of("UTC"));
//        System.out.println("----------");
//        System.out.println("business");
//        System.out.println(openHourUTC);
//        System.out.println(closeHourUTC);
//        System.out.println("submitted");
//        System.out.println(utcStartDateTime);
//        System.out.println(utcEndDateTime);



       //check to see if the hours submitted are within business hours in UTC time. if within returns true else false
        if (openHourUTC.isBefore(utcStartDateTime) && closeHourUTC.isAfter(utcEndDateTime)
                && utcStartDateTime.isBefore(utcEndDateTime)) {
            businessHourCheck = true;
        } else {
            businessHourCheck = false;
            if (utcStartDateTime.isAfter(utcEndDateTime)) {
                errorMessage.setContentText("Appointment start time is after end time");
            } else {
                errorMessage.setContentText("Appointment is outside of Business Hours");
            }
            errorMessage.setTitle("Time Conflict");
            errorMessage.showAndWait();

        }

        //if time within business hours then checks against all appointments. If it finds a conflict it returns false and breaks out of loop
        //each if below is checks a certain way that an appointment can be overlapping.
        if (businessHourCheck) {
            for (Appointment a : allAppointments) {
                ZonedDateTime localStartTime=a.getStartTime().withZoneSameInstant(ZoneId.of(String.valueOf(ZoneId.systemDefault())));
                ZonedDateTime localEndTime=a.getEndTime().withZoneSameInstant(ZoneId.of(String.valueOf(ZoneId.systemDefault())));

                //Checks to see if the current appointment in the list is on the same day
                if (a.getStartTime().toLocalDate().isEqual(utcStartDateTime.toLocalDate())) {

                    if (utcStartDateTime.isAfter(a.getStartTime()) && utcEndDateTime.isBefore(a.getEndTime())) {
                        conflictingAppointments = false;
                        errorMessage.setContentText("Conflicting Appointment from: " + localStartTime.format(formatter) + "\n till " +
                                localEndTime.format(formatter));
                        System.out.println("1");
                        break;
                    } else if (utcStartDateTime.isBefore(a.getStartTime()) && utcEndDateTime.isBefore(a.getEndTime()) &&
                            utcEndDateTime.isAfter(a.getStartTime())) {
                        conflictingAppointments = false;
                        errorMessage.setContentText("Conflicting Appointment from: " + localStartTime.format(formatter) + "\n till " +
                                localEndTime.format(formatter));
                        System.out.println("2");
                        break;
                    } else if (utcStartDateTime.isBefore(a.getEndTime()) && utcEndDateTime.isAfter(a.getEndTime()) &&
                            utcStartDateTime.isAfter(a.getStartTime())) {
                        conflictingAppointments = false;
                        errorMessage.setContentText("Conflicting Appointment from: " + localStartTime.format(formatter) + "\n till " +
                                localEndTime.format(formatter));
                        System.out.println("3");
                        break;
                    } else if (utcStartDateTime.isBefore(a.getStartTime()) && utcEndDateTime.isAfter(a.getEndTime())) {
                        conflictingAppointments = false;
                        errorMessage.setContentText("Conflicting Appointment from: " + localStartTime.format(formatter) + "\n till " +
                                localEndTime.format(formatter));
                        System.out.println("4");
                        break;
                    } else if (utcStartDateTime.isBefore(a.getStartTime()) && utcEndDateTime.isEqual(a.getEndTime())) {
                        conflictingAppointments = false;
                        errorMessage.setContentText("Conflicting Appointment from: " + localStartTime.format(formatter) + "\n till " +
                                localEndTime.format(formatter));
                        System.out.println("5");
                        break;
                    } else if (utcStartDateTime.isAfter(a.getStartTime()) && utcEndDateTime.isEqual(a.getEndTime())) {
                        conflictingAppointments = false;
                        errorMessage.setContentText("Conflicting Appointment from: " + localStartTime.format(formatter) + "\n till " +
                                localEndTime.format(formatter));
                        System.out.println("6");
                        break;
                    } else if (utcStartDateTime.isEqual(a.getStartTime()) && utcEndDateTime.isEqual(a.getEndTime())) {
                        conflictingAppointments = false;
                        errorMessage.setContentText("Conflicting Appointment from: " + localStartTime.format(formatter) + "\n till " +
                                localEndTime.format(formatter));
                        System.out.println("7");
                        break;
                    } else if (utcStartDateTime.isEqual(a.getStartTime()) && utcEndDateTime.isAfter(a.getEndTime())) {
                        conflictingAppointments = false;
                        errorMessage.setContentText("Conflicting Appointment from: " + localStartTime.format(formatter) + "\n till " +
                                localEndTime.format(formatter));
                        System.out.println("8");
                        break;
                    } else {
                        conflictingAppointments = true;
                    }


                } else {
                    conflictingAppointments = true;
                }
            }

        }

        //if time does not conflict and is within business hours returns true. else returns false and display error message
        if (conflictingAppointments && businessHourCheck) {
            return true;
        } else {
            errorMessage.setTitle("Conflicting Appointments");
            errorMessage.showAndWait();
            return false;
        }


    }

    /**
     * @param utcStartDateTime - start time of appt
     * @param utcEndDateTime - end time of appt
     * @param allAppointments - list of all appt
     * @param modifiedAppointmentID - ID of appt to modify
     * @return t/f based off of checkbusinesshours method above
     * Lambda function here to sort through list of appointments by ID and as long as the ID doesnt match the appointment
     * that needs to be modified it gets add to the temp list.
     */
    public boolean modifyAppointmentCheck(ZonedDateTime utcStartDateTime, ZonedDateTime utcEndDateTime,
                                          ObservableList<Appointment> allAppointments, Integer modifiedAppointmentID) {
        ObservableList<Appointment> tempList = FXCollections.observableArrayList();

        //loops through all appointments while Appointment A  id does not equal the modified appointment it adds it to a
        //temp list to then pass that list to check business hours. This is so that when it checks all appointments it does
        //not try to check its times against itself.

        allAppointments.forEach((a)-> {
            if (a.getAppointmentID() != modifiedAppointmentID) {
                tempList.add(a);
            }
        });



        boolean checkAppointment = checkBusinessHours(utcStartDateTime, utcEndDateTime, tempList);
        return checkAppointment;


    }

}

