package cn.qihangerp.open.idosell.utils;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
public class PartNumberUtils {
    /**
     * Extracts the part number from a string like "588C-12A BLACK".
     *
     * @param input The input string containing part number and description
     * @return The extracted part number, or the original string if no space is found
     */
    public static String extractPartNumber(String input) {
        if (input == null || input.isEmpty()) {
            return "";
        }

        // Split by space and take the first part
        int spaceIndex = input.indexOf(" ");
        if (spaceIndex > 0) {
            return input.substring(0, spaceIndex);
        }

        // If no space found, return the original string
        return input;
    }

    /**
     * Alternative method using regular expression to extract part numbers.
     * This handles more complex patterns if needed.
     */
    public static String extractPartNumberRegex(String input) {
        if (input == null || input.isEmpty()) {
            return "";
        }

        // Regex to match alphanumeric and dash characters at the start up to the first space
        Pattern pattern = Pattern.compile("^([\\w-]+)");
        Matcher matcher = pattern.matcher(input);

        if (matcher.find()) {
            return matcher.group(1);
        }

        return "";
    }

    /**
     * 移除字符串的第一个单词，返回剩余部分。
     * 例如从 "Białe sneakersy zdobione brokatem i cyrkoniami Savina"
     * 提取 "sneakersy zdobione brokatem i cyrkoniami Savina"
     *
     * @param input 包含产品描述的输入字符串
     * @return 移除第一个单词后的字符串，如果没有空格则返回空字符串
     */
    public static String removeFirstWord(String input) {
        if (input == null || input.isEmpty()) {
            return "";
        }

        // 按空格分割并取第一个空格后的所有内容
        int spaceIndex = input.indexOf(" ");
        if (spaceIndex > 0 && spaceIndex < input.length() - 1) {
            return input.substring(spaceIndex + 1);
        }

        // 如果没有找到空格或空格后没有内容，返回空字符串
        return "";
    }

    /**
     * 使用正则表达式移除第一个单词的替代方法。
     */
    public static String removeFirstWordRegex(String input) {
        if (input == null || input.isEmpty()) {
            return "";
        }

        // 匹配第一个单词后的所有内容
        Pattern pattern = Pattern.compile("^\\S+\\s+(.+)$");
        Matcher matcher = pattern.matcher(input);

        if (matcher.find()) {
            return matcher.group(1);
        }

        return "";
    }

    public static void main(String[] args) {
        String productCode = "588C-12A BLACK";

        String partNumber = PartNumberUtils.extractPartNumber(productCode);
        System.out.println("Part Number: " + partNumber); // Output: 588C-12A

        // Using the regex alternative
        String partNumberRegex = PartNumberUtils.extractPartNumberRegex(productCode);
        System.out.println("Part Number (Regex): " + partNumberRegex); // Output: 588C-12A

        // 示例2：移除第一个单词
        String productDesc = "Białe sneakersy zdobione brokatem i cyrkoniami Savina";
        String description = PartNumberUtils.removeFirstWord(productDesc);
        System.out.println("商品描述: " + description); // 输出: sneakersy zdobione brokatem i cyrkoniami Savina

        // 使用正则表达式的替代方法
        String descriptionRegex = PartNumberUtils.removeFirstWordRegex(productDesc);
        System.out.println("商品描述 (正则): " + descriptionRegex); // 输出相同结果
    }
}
