package Model;

/**
 * This class is used to model report objects.
 */

public class Reports {

    /**
     * This inner class is used for report1, returning numbers of different appointment types per month.
     */
    public static class Report1{
        private String appointmentType;
        private Integer appointmentCount;

        /**
         * Constructor for Report1 creates a new report object in order to populate the table
         * @param appointmentType hard-coded types of appointments
         * @param appointmentCount counts each type per month
         */
        public Report1(String appointmentType, Integer appointmentCount) {
            this.appointmentType = appointmentType;
            this.appointmentCount = appointmentCount;
        }

        public String getAppointmentType() {
            return appointmentType;
        }

        public Integer getAppointmentCount() {
            return appointmentCount;
        }
    }

}
