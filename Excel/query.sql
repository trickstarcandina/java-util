for(Integer name: data.keySet()){
    Query sqlQuery = .....("UPDATE TABLE " +
        "SET .... " +
        "WHERE ....")
                .setParameter(.....);
    sqlQuery.executeUpdate();
}