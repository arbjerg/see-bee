
module com.namely.seebee.typemaper.standard {
    requires com.namely.seebee.typemapper;
    
    provides com.namely.seebee.typemapper.TypeMapper 
        with com.namely.seebee.typemapper.standard.StringTypeMapper;
    
//    provides com.namely.seebee.typemapper.TypeMapper 
//        with com.namely.seebee.typemapper.standard.IntegerTypeMapper;
}



