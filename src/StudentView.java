public final class StudentView {

    public void display(Student student) {
        System.out.println("Student ID: " + student.getStudentId());
        System.out.println("Name: " + student.getName());
        System.out.println("Type: " + student.getStudentType());
        System.out.println("Average: %.2f%%".formatted(
                student.calculateAverageGrade()
        ));
        System.out.println("Status: " + student.getStatus());
    }
}