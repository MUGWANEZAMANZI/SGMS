import java.time.LocalDateTime;

public record Grade(
        String id,
        String subject,
        SubjectType subjectType,
        double value,
        LocalDateTime recordedAt
) {
    private static int nextGradeNumber = 1;

    public Grade(String subject, SubjectType subjectType, double value) {
        this(
                "GRD%03d".formatted(nextGradeNumber++),
                subject,
                subjectType,
                value,
                LocalDateTime.now()
        );
    }

    public Grade {
        if (subject == null || subject.isBlank()) {
            throw new IllegalArgumentException("Subject cannot be blank");
        }
        if (subjectType == null) {
            throw new IllegalArgumentException("Subject type is required");
        }
        if (value < 0 || value > 100) {
            throw new IllegalArgumentException("Grade must be between 0 and 100");
        }
    }

    public String formattedDate() {
        return recordedAt.toLocalDate().toString();
    }
}

enum SubjectType {
    CORE,
    ELECTIVE
}
