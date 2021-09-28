public class Main {
    public static void main(String[] args) throws IOException {
        FileInputStream file = new FileInputStream(new File("E:/abc.xlsx")); //path location file
        Workbook workbook = new XSSFWorkbook(file);
        //or Workbook workbook = new XSSFWorkbook(file.getInputStream());
        Sheet sheet = workbook.getSheetAt(0);
        Map<Integer, List<String>> data = new HashMap<>();
        int i = 0;
        for (Row row : sheet) {
            data.put(i, new ArrayList<String>());
            for (Cell cell : row) {
                switch (cell.getCellTypeEnum()) {
                    case STRING:
                        data.get(new Integer(i)).add(cell.getRichStringCellValue().getString());
                        break;
                    case NUMERIC:
                        if (DateUtil.isCellDateFormatted(cell)) {
                            data.get(i).add(cell.getDateCellValue() + "");
                        } else {
                            data.get(i).add(cell.getNumericCellValue() + "");
                        }
                        break;
                    case BOOLEAN:
                        data.get(i).add(cell.getBooleanCellValue() + "");
                        break;
                    case FORMULA:
                        data.get(i).add(cell.getCellFormula() + "");
                        break;
                    default:
                        data.get(new Integer(i)).add(" ");
                }
            }
            i++;
        }
//        for(Integer name: data.keySet()){
//            String key = name.toString();
//            String value = data.get(name).toString();
////            System.out.println(key);
////            for(int s=0;s<=data.get(name).size();s++){
////                System.out.println(data.get(name).get(s));
////            }
//            for(String s : data.get(name)){
//                System.out.println(s);
//            }
//        }

        for (int j = 1; j < data.size(); j++) {
            for (String s : data.get(j)) {
                System.out.println(s);
            }
        }
    }
}