package report;

/**
 * Serializable student data used by importers.
 *
 * @param name student's name
 * @param age student's age
 * @param email student's email
 * @param phone student's phone
 * @param type student's category
 * @param status student's status
 */
public record StudentRecord(
        String name,
        int age,
        String email,
        String phone,
        String type,
        String status) {
}
