package fr.hd3d.colortribe.core.probes;

import fr.hd3d.colortribe.color.type.Point3f;


public interface IProbe
{
    public static enum EProbeType
    {
        TEST_CURVE_PROBE("Demo probe", "img/testprobe.png"), MK_CS200("Minolta CS-200", "img/cs200.png"), SPYDER_3("DataColor Spyder 3", "img/spyder3.png"), SPYDER_4("DataColor Spyder 4", "img/spyder4.png"), K10("Klein K-10", "img/k10.png"), GAMMA_AND_COLOR_PROBE("Probes couple", "img/testprobe.png"), SPECTRO_SPYDER3("Spectro + Spyder3", "img/testprobe.png");

        private String _name = null;
        private String _image = null;

        private EProbeType(String name, String image)
        {
            _name = name;
            _image = image;
        }

        public String getName()
        {
            return _name;
        }
        
        public String toString(){
            return _name;    
         } 
        
        public String getImage(){
            return _image;
        }

    }

    public boolean isAvailable(String comPort) throws Exception;  
    public boolean isOpen() ;
    public boolean open(String comPort) throws Exception;
    public void close();
    public Point3f readXYZ() throws Exception;
    public String getProbeDescription();
    public EProbeType getEProbeType();
    public String getSerialInfo();

}
