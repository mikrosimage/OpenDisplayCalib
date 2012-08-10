package fr.hd3d.colortribe.core;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import fr.hd3d.colortribe.color.EStandardIlluminants;
import fr.hd3d.colortribe.color.IIlluminant;
import fr.hd3d.colortribe.color.IRgbPrimary;
import fr.hd3d.colortribe.color.Illuminant;
import fr.hd3d.colortribe.color.RgbPrimaries;
import fr.hd3d.colortribe.color.type.Point2f;
import fr.hd3d.colortribe.color.type.Point3f;
import fr.hd3d.colortribe.com.CHSocketServer;
import fr.hd3d.colortribe.com.ISocketServer;
import fr.hd3d.colortribe.com.UnpluggedSocketServer;
import fr.hd3d.colortribe.core.correction.AbstractCorrection;
import fr.hd3d.colortribe.core.correction.ENSLLColorCorrection;
import fr.hd3d.colortribe.core.correction.WhiteSoftwareCorrection;
import fr.hd3d.colortribe.core.probes.AbstractProbe;
import fr.hd3d.colortribe.core.probes.ProbesPool;
import fr.hd3d.colortribe.core.probes.IProbe.EProbeType;
import fr.hd3d.colortribe.core.protocols.AbstractProtocol;
import fr.hd3d.colortribe.core.target.ITarget;


public class ColorHealerModel
{

    public static final ColorHealerModel _instance = new ColorHealerModel();
    private DisplayDevice _dispDev = null;
    private AbstractProtocol _protocol = null;
    private String _clientName = null;
    private ProbesPool _probesPool = null;
    private AbstractProbe _probe = null;
    private ITarget _target = null;
    private List<MeasuresSet> _sets = null;
    private List<AbstractCorrection> _corrections = null;
    private int _currentMeasuresSetIndex = -1;
    private int _currentCorrectionIndex = -1;
    private MeasuresSet _tmpMeasuresSet;
    private AbstractCorrection _tmpCorrection;
    private MultiplexedCorrection _multiCorrection;
    private boolean softWhiteCorrectionEnable = false;
    private WhiteSoftwareCorrection softCorrection = null;
    private List<IIlluminant> _customIlluminants = null;
    private List<IRgbPrimary> _customPrimaries = null;
    private ISocketServer socketServer = null;
    private String _venue = null;
    // only for projector
    private int _bulbHoursCount = 0;
    private String _calibrationFormat = "";

    private boolean isCalibUpdated = false;

    public boolean isCalibUpdated()
    {
        return isCalibUpdated;
    }

    public ISocketServer getSocketServer()
    {
        return socketServer;
    }

    public void setCalibUpdated(boolean isCalibUpdated)
    {
        this.isCalibUpdated = isCalibUpdated;
    }

    public void unplugSocketServer()
    {
        if (socketServer != null)
        {
            socketServer.closeServer();
            socketServer = UnpluggedSocketServer.getInstance();
        }
    }

    public void setProtocol(AbstractProtocol protocol)
    {
        _protocol = protocol;
    }

    public static String getFormatDate()
    {
        Date creationDate = new Date();
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTime(creationDate);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH) + 1;
        int year = calendar.get(Calendar.YEAR);
        return day + "_" + month + "_" + year;
    }

    private ColorHealerModel()
    {

        socketServer = CHSocketServer.getInstance();
        _probesPool = new ProbesPool();
        _sets = new ArrayList<MeasuresSet>();
        _corrections = new ArrayList<AbstractCorrection>();
        _tmpMeasuresSet = new MeasuresSet();
        _tmpCorrection = new ENSLLColorCorrection(-1);
        _multiCorrection = new MultiplexedCorrection();
        // custom illuminants
        _customIlluminants = new ArrayList<IIlluminant>();
        File file = new File("custom_parameters/custom_illuminants.txt");
        try
        {
            BufferedReader reader = new BufferedReader(new FileReader(file));

            int beg;
            String line = null;
            String name;

            while ((line = reader.readLine()) != null)
            {
                Point2f point = new Point2f();
                beg = line.indexOf(" ");
                name = line.substring(0, beg);
                line = line.substring(beg + 1);
                beg = line.indexOf(" ");
                point._a = Float.parseFloat(line.substring(0, beg));
                line = line.substring(beg + 1);
                beg = line.indexOf(" ");
                point._b = Float.parseFloat(line.substring(0, beg));
                line = line.substring(beg + 1);

                _customIlluminants.add(new Illuminant((int) EStandardIlluminants.getApproximateColorTemperature(point),
                        name, point, line));
            }
            reader.close();
        }
        catch (FileNotFoundException e1)
        {
            System.err.println("File " + file.getName() + " was not found. No custom illuminants declared.");

        }
        catch (IOException e2)
        {
            System.err.println("File " + file.getName() + " can't be read. No custom illuminants declared.");

        }
        // custom primaries
        _customPrimaries = new ArrayList<IRgbPrimary>();
        File file2 = new File("custom_parameters/custom_pirmaries.txt");
        try
        {
            BufferedReader reader = new BufferedReader(new FileReader(file2));

            int beg;
            String line = null;
            String name;

            while ((line = reader.readLine()) != null)
            {
                Point2f r, g, b;
                r = new Point2f();
                g = new Point2f();
                b = new Point2f();
                beg = line.indexOf(" ");
                name = line.substring(0, beg);
                line = line.substring(beg + 1);
                beg = line.indexOf(" ");
                r._a = Float.parseFloat(line.substring(0, beg));
                line = line.substring(beg + 1);
                beg = line.indexOf(" ");
                r._b = Float.parseFloat(line.substring(0, beg));
                line = line.substring(beg + 1);
                beg = line.indexOf(" ");
                g._a = Float.parseFloat(line.substring(0, beg));
                line = line.substring(beg + 1);
                beg = line.indexOf(" ");
                g._b = Float.parseFloat(line.substring(0, beg));
                line = line.substring(beg + 1);
                beg = line.indexOf(" ");
                b._a = Float.parseFloat(line.substring(0, beg));
                line = line.substring(beg + 1);
                beg = line.indexOf(" ");
                b._b = Float.parseFloat(line.substring(0, beg));
                line = line.substring(beg + 1);

                IIlluminant red = new Illuminant((int) EStandardIlluminants.getApproximateColorTemperature(r), name
                        + "_red", r);
                IIlluminant green = new Illuminant((int) EStandardIlluminants.getApproximateColorTemperature(g), name
                        + "_green", g);
                IIlluminant blue = new Illuminant((int) EStandardIlluminants.getApproximateColorTemperature(b), name
                        + "_blue", b);

                _customPrimaries.add(new RgbPrimaries(name, red, green, blue, line));
            }
            reader.close();
        }
        catch (FileNotFoundException e1)
        {
            System.err.println("File " + file.getName() + " was not found. No custom primaries declared.");
        }
        catch (IOException e2)
        {
            System.err.println("File " + file.getName() + " can't be read. No custom primaries declared.");
        }

    }

    public List<IIlluminant> getCustomIlluminants()
    {
        return _customIlluminants;
    }

    public List<IRgbPrimary> getCustomPrimaries()
    {
        return _customPrimaries;
    }

    public WhiteSoftwareCorrection getSoftWhiteCorrection()
    {
        return softCorrection;
    }

    public void setSoftWhiteCorrection(WhiteSoftwareCorrection softWhiteCorrection)
    {
        this.softCorrection = softWhiteCorrection;
    }

    public boolean isSoftWhiteCorrectionEnable()
    {
        return softWhiteCorrectionEnable;
    }

    public void setSoftWhiteCorrectionEnable(boolean softWhiteCorrectionEnable)
    {
        this.softWhiteCorrectionEnable = softWhiteCorrectionEnable;
    }

    public MeasuresSet getCurrentMeasuresSet()
    {
        if (_currentMeasuresSetIndex < 0)
            return _tmpMeasuresSet;
        return _sets.get(_currentMeasuresSetIndex);
    }

    public MeasuresSet getMeasuresSet(int index)
    {
        if (index < 0)
            return _tmpMeasuresSet;
        return _sets.get(index);
    }

    public MeasuresSet getBasicMeasuresSet()
    {
        return _tmpMeasuresSet;
    }

    public AbstractCorrection getCorrection()
    {
        return _multiCorrection;// _corrections.get(_currentCorrectionIndex);
    }

    public int getNumberOfCorrection()
    {
        return _corrections.size();
    }

    public void addMeasuresSet()
    {
        _sets.add(new MeasuresSet());
        _currentMeasuresSetIndex = _sets.size() - 1;
        _corrections.add(new ENSLLColorCorrection(_currentMeasuresSetIndex));

    }

    public void removeLastMeasuresSet()
    {
        _sets.remove(_sets.size() - 1);
        _currentMeasuresSetIndex = _sets.size() - 1;
        _corrections.remove(_corrections.size() - 1);

    }

    public void setCurrentMeasuresSet(int index)
    {
        _currentMeasuresSetIndex = index;
    }

    public void setCurrentCorrection(int index)
    {
        _currentCorrectionIndex = index;

    }

    public int getCurrentMeasuresSetIndex()
    {
        return _currentMeasuresSetIndex;
    }

    public int getCurrentCorrectionIndex()
    {
        return _currentCorrectionIndex;
    }

    public DisplayDevice getDisplayDevice()
    {
        return _dispDev;
    }

    public DisplayDevice setDisplayDevice(String screenSocketInfo)
    {
        _dispDev = new DisplayDevice(screenSocketInfo);
        return _dispDev;
    }

    public DisplayDevice initDisplayDevice()
    {
        _dispDev = new DisplayDevice();
        return _dispDev;
    }

    public void setClientName(String name)
    {
        _clientName = name;
    }

    public String getClientName()
    {
        return _clientName;
    }

    public void setVenue(String venue)
    {
        _venue = venue;
    }

    public String getVenue()
    {
        return _venue;
    }

    public void setBulbHoursCount(int hours)
    {
        _bulbHoursCount = hours;
    }

    public int getBulbHoursCount()
    {
        return _bulbHoursCount;
    }

    public void setCalibrationFormat(String format)
    {
        _calibrationFormat = format;
    }

    public String getCalibrationFormat()
    {
        return _calibrationFormat;
    }

    public AbstractProtocol getProtocol()
    {
        return _protocol;
    }

    public ProbesPool getProbesPool()
    {
        return _probesPool;
    }

    public ITarget getTarget()
    {
        return _target;
    }

    public void setTarget(ITarget target)
    {
        _target = target;
    }

    public void setProbe(EProbeType probeType)
    {
        _probe = _probesPool.getProbe(probeType);
    }

    public AbstractProbe getProbe()
    {
        return _probe;
    }

    private class MultiplexedCorrection extends AbstractCorrection
    {

        public MultiplexedCorrection()
        {
            _redCorrection = new ArrayList<Point2f>();
            _blueCorrection = new ArrayList<Point2f>();
            _greenCorrection = new ArrayList<Point2f>();
        }

        @Override
        public void computeColorCorrection()
        {

            AbstractCorrection currentCorr;
            if (_currentCorrectionIndex != -1)
                currentCorr = _corrections.get(_currentCorrectionIndex);
            else
                currentCorr = _tmpCorrection;

            currentCorr.computeColorCorrection();
            _redCorrection.clear();
            _blueCorrection.clear();
            _greenCorrection.clear();
            int range = currentCorr.getRedCorrection().size();
            for (int i = 0; i < currentCorr.getRedCorrection().size(); i++)
            {
                _redCorrection.add(new Point2f(currentCorr.getRedCorrection().get(i)));
            }
            for (int i = 0; i < currentCorr.getGreenCorrection().size(); i++)
            {
                _greenCorrection.add(new Point2f(currentCorr.getGreenCorrection().get(i)));
            }
            for (int i = 0; i < currentCorr.getBlueCorrection().size(); i++)
            {
                _blueCorrection.add(new Point2f(currentCorr.getBlueCorrection().get(i)));
            }
            if (isSoftWhiteCorrectionEnable())
            {
                for (int i = 0; i < range; i++)
                {
                    float x = i / (float) (range - 1);
                    _redCorrection.get(i)._b += softCorrection.getValue(0, x) - 1 * x;
                    _greenCorrection.get(i)._b += softCorrection.getValue(1, x) - 1 * x;
                    _blueCorrection.get(i)._b += softCorrection.getValue(2, x) - 1 * x;
                }
            }
        }

        // @Override
        // public void computeColorCorrection()
        // {
        // for (int i = 0; i <= _currentCorrectionIndex; i++)
        // _corrections.get(i).computeColorCorrection();
        //            
        // AbstractCorrection currentCorr = _corrections.get(0);
        // _redCorrection.clear();
        // _blueCorrection.clear();
        // _greenCorrection.clear();
        // int range = currentCorr.getRedCorrection().size();
        // for (int i = 0; i < range; i++)
        // {
        // _redCorrection.add(new Point2f(currentCorr.getRedCorrection().get(i)));
        // _greenCorrection.add(new Point2f(currentCorr.getGreenCorrection().get(i)));
        // _blueCorrection.add(new Point2f(currentCorr.getBlueCorrection().get(i)));
        // }
        // for (int i = 1; i <= _currentCorrectionIndex; i++)
        // {
        // currentCorr = _corrections.get(i);
        // List<Point2f> currentReds = currentCorr.getRedCorrection();
        // List<Point2f> currentGreens = currentCorr.getGreenCorrection();
        // List<Point2f> currentBlues = currentCorr.getBlueCorrection();
        // int maxValue = range - 1;
        // for (int j = 0; j < range; j++)
        // {
        // int redIndex = (int) (_redCorrection.get(j)._b * maxValue);
        // int greenIndex = (int) (_greenCorrection.get(j)._b * maxValue);
        // int blueIndex = (int) (_blueCorrection.get(j)._b * maxValue);
        // _redCorrection.get(j)._b = currentReds.get(redIndex)._b;
        // _greenCorrection.get(j)._b = currentGreens.get(greenIndex)._b;
        // _blueCorrection.get(j)._b = currentBlues.get(blueIndex)._b;
        // }
        // }
        //            
        // if (isSoftWhiteCorrectionEnable())
        // {
        // for (int i = 0; i < range; i++)
        // {
        // float x = i / (float) (range - 1);
        // _redCorrection.get(i)._b += softCorrection.getValue(0, x) - 1 * x;
        // _greenCorrection.get(i)._b += softCorrection.getValue(1, x) - 1 * x;
        // _blueCorrection.get(i)._b += softCorrection.getValue(2, x) - 1 * x;
        // }
        // }
        // }

        @Override
        public String getSummary()
        {
            String sumary = ":: Correction(s) ::\n";
            for (int i = 0; i < _corrections.size(); i++)
            {
                sumary += _corrections.get(i).getSummary() + "\n";
            }
            sumary += "--> Correction " + _currentCorrectionIndex + " is applied.\n";
            if (isSoftWhiteCorrectionEnable())
                sumary += "White software correction is enable with : R " + softCorrection.getRedMaxValue() + " G "
                        + softCorrection.getGreenMaxValue() + " B " + softCorrection.getGreenMaxValue();
            return sumary;
        }

        @Override
        public Point3f getDelta()
        {
            if (_currentCorrectionIndex == -1)
                return _tmpCorrection.getDelta();
            return _corrections.get(_currentCorrectionIndex).getDelta();
        }

        @Override
        public Point3f getComputeGamma()
        {
            if (_currentCorrectionIndex == -1)
                return _tmpCorrection.getComputeGamma();
            return _corrections.get(_currentCorrectionIndex).getComputeGamma();
        }

        @Override
        public Point3f getCalibratedDelta()
        {
            if (_currentCorrectionIndex == -1)
                return _tmpCorrection.getCalibratedDelta();
            return _corrections.get(_currentCorrectionIndex).getCalibratedDelta();
        }

        @Override
        public void setMeasuredGamma(MeasuresSet set)
        {
            if (_currentCorrectionIndex == -1)
                _tmpCorrection.setMeasuredGamma(set);
            else
                _corrections.get(_currentCorrectionIndex).setMeasuredGamma(set);

        }

        @Override
        public Point3f getCalibratedGamma()
        {
            if (_currentCorrectionIndex == -1)
                return _tmpCorrection.getCalibratedGamma();

            return _corrections.get(_currentCorrectionIndex).getCalibratedGamma();
        }

    }

    public Point3f getCorrectionDelta(int correctionIndex)
    {

        if (_currentCorrectionIndex == -1)
            return _tmpCorrection.getCalibratedDelta();
        return _corrections.get(correctionIndex).getCalibratedDelta();
    }
}
