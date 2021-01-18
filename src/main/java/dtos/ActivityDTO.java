package dtos;

import entities.Activity;


public class ActivityDTO {
    
    private String exerciseDate;
    private String exerciseType;
    private String timeOfDay;
    private String duration;
    private String distance;
    private String comment;

    public ActivityDTO() {
    }

    public ActivityDTO(Activity activity) {
        this.exerciseDate = activity.getExerciseDate();
        this.exerciseType = activity.getExerciseType();
        this.timeOfDay = activity.getTimeOfDay();
        this.duration = activity.getDuration();
        this.distance = activity.getDistance();
        this.comment = activity.getComment();
    }

    public String getExerciseDate() {
        return exerciseDate;
    }

    public void setExerciseDate(String exerciseDate) {
        this.exerciseDate = exerciseDate;
    }

    public String getExerciseType() {
        return exerciseType;
    }

    public void setExerciseType(String exerciseType) {
        this.exerciseType = exerciseType;
    }

    public String getTimeOfDay() {
        return timeOfDay;
    }

    public void setTimeOfDay(String timeOfDay) {
        this.timeOfDay = timeOfDay;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
    
    
    
    
}
