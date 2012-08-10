package fr.hd3d.colortribe.core;

public class DisplayDevice
{
    private int _osIndex;
    private String _uid = null;
    private String _manufacturer = null;
    private String _model = null;
    private EDisplayDeviceType _type = null;
    private String _modelID = null;
    private String _profilName = null;
    
    public void setUid(String uid)
    {
        _uid = uid;
    }

    public void setManufacturer(String manufacturer)
    {
        _manufacturer = manufacturer;
    }

    public void setModel(String model)
    {
        _model = model;
    }

    public enum EDisplayDeviceType{
        LCD("LCD","img/lcd.png"), CRT("CRT", "img/crt.png"), PROJECTOR("PROJECTOR","img/projector.png");
        private String _string;
        private String _image;
        private EDisplayDeviceType(String string, String image){
            _string = string;
            _image = image;
        }
    
        public String toString(){
            return _string;    
         } 
        public String getImage(){
            return _image;
        }

        static public EDisplayDeviceType toType(String s){
           if(s.compareToIgnoreCase("LCD")==0)
            return LCD;
           else if (s.compareToIgnoreCase("CRT")==0)
               return CRT;
           else if (s.compareToIgnoreCase("PROJECTOR")==0)
               return PROJECTOR;
           else return null;
        }
    }; 
    
    public DisplayDevice(){
     
    }

    DisplayDevice(String screenInfo) throws IllegalArgumentException
    {
        // SCREEN_INFO_ [index]1;[uid]ENC1816 | numeric UID unset | S/N
        // 53897067;[manufacturer]ENC;[model]S2000;[type]unset
        String[] cuts = screenInfo.split(";");
        if (cuts.length == 6)
        {
            int index = cuts[0].indexOf("]") + 1;
            _osIndex = cuts[0].charAt(index);
            index = cuts[1].indexOf("]") + 1;
            _uid = cuts[1].substring(index);
            index = cuts[2].indexOf("]") + 1;
            _manufacturer = cuts[2].substring(index);
            index = cuts[3].indexOf("]") + 1;
            _model = cuts[3].substring(index);
            index = cuts[4].indexOf("]") + 1;
            
            _type = EDisplayDeviceType.toType(cuts[4].substring(index));
            index = cuts[5].indexOf("]") + 1;
            _profilName = cuts[5].substring(index);
            

        }
        else
        {
            throw new IllegalArgumentException("Unvalid DisplayDevice description string");
        }
        
        cuts = _uid.split(" | ");
        _modelID = cuts[0];
        
    }
    
    public boolean isTypeSet(){
        return ! (_type == null);
    }
    
    public String getUid()
    {
        return _uid;
    }
    public String getModelId()
    {
        return _modelID;
    }

    public int getOsIndex()
    {
        return _osIndex;
    }

    public String getManufacturer()
    {
        return _manufacturer;
    }
    
    public String getProfilName()
    {
        return _profilName;
    }

    public String getModel()
    {
        return _model;
    }

    public EDisplayDeviceType getType()
    {
        
        return _type;
    }
    public String getStringType()
    {
        if(isTypeSet())
        return _type.toString();
        else return "unset";
    }
    
    public void setType(EDisplayDeviceType type)
    {
        _type = type;
    }
}
