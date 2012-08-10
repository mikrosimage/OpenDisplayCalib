package fr.hd3d.colortribe.core.protocols;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import fr.hd3d.colortribe.core.ColorHealerModel;
import fr.hd3d.colortribe.core.MeasuresSet;
import fr.hd3d.colortribe.core.correction.AbstractCorrection;
import fr.hd3d.colortribe.gui.steps.BeforeStep;
import fr.hd3d.colortribe.gui.steps.CorrectionStep;
import fr.hd3d.colortribe.gui.steps.DisplayStep;
import fr.hd3d.colortribe.gui.steps.FinalisationStep;
import fr.hd3d.colortribe.gui.steps.LuminosityContrastStep;
import fr.hd3d.colortribe.gui.steps.MeasuresStep;
import fr.hd3d.colortribe.gui.steps.ProbeTargetStep;
import fr.hd3d.colortribe.gui.steps.Step;
import fr.hd3d.colortribe.gui.steps.ValidationStep;
import fr.hd3d.colortribe.gui.steps.WhiteStep;
import fr.hd3d.colortribe.gui.steps.Step.StepStatus;


public class CalibrationProtocol extends AbstractProtocol
{
    private LinkedHashMap<String, Step> _steps;
    private String _selectedStep;
    private boolean shouldAbort;

    public CalibrationProtocol()
    {
        _steps = new LinkedHashMap<String, Step>();
        _steps.put(BeforeStep.NAME, new BeforeStep());
        _steps.put(DisplayStep.NAME, new DisplayStep());
        _steps.put(ProbeTargetStep.NAME, new ProbeTargetStep(StepStatus.DISABLE));
        _steps.put(LuminosityContrastStep.NAME, new LuminosityContrastStep());
        _steps.put(WhiteStep.NAME, new WhiteStep());
        _steps.put(MeasuresStep.NAME, new MeasuresStep());
        _steps.put(CorrectionStep.NAME, new CorrectionStep());
        _steps.put(ValidationStep.NAME, new ValidationStep());
        _steps.put(FinalisationStep.NAME, new FinalisationStep());
        _selectedStep = "Before";

        _steps.get(_selectedStep).init();

    }

    public LinkedHashMap<String, Step> getSteps()
    {
        return _steps;
    }

    public StepStatus getStepStatus(String name)
    {
        Step step = _steps.get(name);
        if (step != null)
            return step.getStatus();
        else
            return StepStatus.UNKNOWN;
    }

    

    public String getStepHTMLDescription(String name)
    {
        Step step = _steps.get(name);
        if (step != null)
            return step.getHTMLDescription();
        else
            return "UNKNOWN";
    }

    public Step getSelectedStep()
    {
        return _steps.get(_selectedStep);
    }

    public void setSelectedStep(String name)
    {
        _selectedStep = name;
    }

    public void unLockStep(String name)
    {
        Step step = _steps.get(name);
        if (step != null)
        {
            step.unLock();
        }

    }

    public void lockStep(String name, String reason)
    {
        Step step = _steps.get(name);
        if (step != null)
        {
            step.lock(reason);
        }

    }

    private void abortAndClean()
    {

    }

    private void addRegularIntervalPatches(List<Color> patches, int nbPatches, float gamma)
    {
        float step = 1 / (float) (nbPatches);
        for (int icolor = 0; icolor < 3; icolor++)
        {
            for (int i = 1; i < nbPatches; i++)
            {
                int x = (int) (Math.pow(i * step, 1 / gamma) * 255);
                if (icolor == 1)
                {
                    patches.add(new Color(x, 0, 0));

                }
                else if (icolor == 2)
                {
                    patches.add(new Color(0, x, 0));
                }
                else
                {
                    patches.add(new Color(0, 0, x));
                }
            }
        }
    }

    private void checkLine(String line) throws ParseException
    {
        if (line == null)
            throw new ParseException("Custom patches parsing error", 0);
    }

    private void parseBlock(BufferedReader reader, String line, List<Color> patches) throws ParseException, IOException
    {
        if (line.contains("REGULAR_INTERVALS"))
        {
            line = reader.readLine();
            checkLine(line);
            int nbPatches = Integer.parseInt(line);
            line = reader.readLine();
            checkLine(line);
            float gamma = Float.parseFloat(line);
            addRegularIntervalPatches(patches, nbPatches, gamma);

        }
        else if (line.contains("CUSTOM_PATCHES"))
        {
            line = reader.readLine();
            checkLine(line);
            int nbPatches = Integer.parseInt(line);
            int beg;
            for (int i = 0; i < nbPatches; i++)
            {
                line = reader.readLine();
                checkLine(line);
                beg = line.indexOf(" ");
                int r = Integer.parseInt(line.substring(0, beg));
                line = line.substring(beg + 1);
                beg = line.indexOf(" ");
                int g = Integer.parseInt(line.substring(0, beg));
                line = line.substring(beg + 1);
                int b = Integer.parseInt(line);
                patches.add(new Color(r, g, b));
            }
        }
        else
            throw new ParseException("Empty file", 0);

    }

    private void createDefaultPatchesFile(File file)
    {
        try
        {
            BufferedWriter write = new BufferedWriter(new FileWriter(file));
            write.append("CUSTOM_PATCHES\n3\n255 0 0\n0 255 0\n0 0 255\nREGULAR_INTERVALS\n15\n1.25\n");
            write.close();
        }
        catch (IOException e)
        {
            System.err.println("Can't create custom_parameters/custom_patches.txt.");
        }
    }

    private List<Color> getPatches()
    {
        return getPatchesFromFile("custom_parameters/custom_patches.txt");
    }

    private List<Color> getCheckPatches()
    {
        return getPatchesFromFile("custom_parameters/custom_check_patches.txt");
    }

    private List<Color> getPatchesFromFile(String filePath)
    {
        List<Color> patches = new ArrayList<Color>();
        File file = new File(filePath);
        BufferedReader reader = null;
        try
        {
            reader = new BufferedReader(new FileReader(file));

            String line = null;
            while ((line = reader.readLine()) != null)
            {
                parseBlock(reader, line, patches);
            }

        }
        catch (FileNotFoundException e1)
        {
            System.err.println("File " + file.getName()
                    + " was not found. No custom illuminants declared.\nDefault file was created.");
            // create it
            createDefaultPatchesFile(file);
        }
        catch (IOException e2)
        {
            System.err.println("File " + file.getName() + " can't be read. No custom illuminants declared.");

        }
        catch (ParseException e3)
        {

        }
        if (reader != null)
        {
            try
            {
                reader.close();
            }
            catch (IOException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        return patches;
    }

    public void launchGammaMeasures()
    {
        ColorHealerModel model = ColorHealerModel._instance;
        MeasuresSet samplesSet = model.getCurrentMeasuresSet();
        try
        {
            int screenIndex = ColorHealerModel._instance.getDisplayDevice().getOsIndex();
            ColorHealerModel._instance.getSocketServer().sendMessage("SET_LUT_OFF " + screenIndex + "\n");
            ColorHealerModel._instance.getSocketServer().sendMessage(
                    "SHOULD_DISPLAY " + screenIndex + " " + false + "\n");

            shouldAbort = false;

            List<Color> patches = getPatches();
            if (patches.size() == 0)
            {
                addRegularIntervalPatches(patches, 15, 1.25f);
            }
            boolean res;
            for (Color color : patches)
            {
                res = samplesSet.mesureThisColor(samplesSet, color, "gamma measure");
                if (!res)
                    return;
                if (shouldAbort)
                {
                    abortAndClean();
                    return;
                }

                notifyMeasuresSetChanged();
            }
            model.setCurrentCorrection(model.getCurrentMeasuresSetIndex());

        }
        catch (IllegalAccessException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

    }

    public void abortGammaMeasures()
    {
        shouldAbort = true;
    }

    public boolean wasGammaMeasuresAborted()
    {
        return shouldAbort;
    }

    public void computeCorrection()
    {
        ColorHealerModel._instance.getCorrection().computeColorCorrection();
    }

    public AbstractCorrection getCorrection()
    {
        return ColorHealerModel._instance.getCorrection();
    }

    public void launchValidationMeasures()
    {
        System.out.println("::: validation measures :::");
        ColorHealerModel model = ColorHealerModel._instance;
        if (model.getCurrentCorrectionIndex() >= 0)
        {
            model.getProtocol().computeCorrection();// TODO ne pas le faire � chaque fois
            model.getProtocol().getCorrection().sendLut();
            try
            {
                int screenIndex = ColorHealerModel._instance.getDisplayDevice().getOsIndex();
                ColorHealerModel._instance.getSocketServer().sendMessage("SHOULD_DISPLAY " + screenIndex + " " + true + "\n");
            }
            catch (IllegalAccessException e1)
            {
                e1.printStackTrace();
            }
            catch (IOException e1)
            {
                e1.printStackTrace();
            }

            MeasuresSet set = new MeasuresSet();
            List<Color> patches = getCheckPatches();
            System.out.println("validation with " + patches.size() + "patches.");

            boolean res;
            for (Color color : patches)
            {
                res = set.mesureThisColor(set, color, "validation measure");
                if (!res)
                    return;

            }

            model.getCorrection().setMeasuredGamma(set);
        }

    }

}
