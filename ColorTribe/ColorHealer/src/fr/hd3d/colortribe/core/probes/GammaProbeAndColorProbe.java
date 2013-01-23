package fr.hd3d.colortribe.core.probes;

import javax.swing.JOptionPane;

import fr.hd3d.colortribe.color.type.Point3f;

public class GammaProbeAndColorProbe extends AbstractProbe {
	private AbstractProbe gammaProbe;
	private AbstractProbe colorProbe;
	private boolean isOpen = false;

	private AbstractProbe selectedProbe = null;
	private final EProbeType probeType;

	public void selectGammaProbe() throws Exception {
		if(selectedProbe == gammaProbe)
			return;
		colorProbe.close();
		selectedProbe = gammaProbe;
		calibrationWarning();
		gammaProbe.open("");

	}

	public void selectColorProbe() throws Exception {
		if(selectedProbe == colorProbe)
			return;
		gammaProbe.close();
		selectedProbe = colorProbe;
		calibrationWarning();
		colorProbe.open("");

	}

	private void calibrationWarning() {
		if (selectedProbe.isSpecificCalibrationRequired()) {
			Object[] options = {"Set up"};
			JOptionPane.showOptionDialog(null,
					selectedProbe.getProbeDescription()
							, "Calibrate "
							+ getSelectedProbeType(), JOptionPane.OK_OPTION, JOptionPane.WARNING_MESSAGE, null,options, options[0]);
		}
	}

	public EProbeType getSelectedProbeType() {
		return selectedProbe.getEProbeType();
	}

	public GammaProbeAndColorProbe(AbstractProbe gammaProbe,
			AbstractProbe colorProbe, EProbeType probeType) {
		this.probeType = probeType;
		isOpen = true;
		this.gammaProbe = gammaProbe;
		this.colorProbe = colorProbe;
	}

	public void close() {
		gammaProbe.close();
		colorProbe.close();
		isOpen = false;
	}

	public EProbeType getEProbeType() {
		return probeType;
	}

	public String getProbeDescription() {

		return colorProbe.getEProbeType().getName() + " will be calibrated before \"White and gray points\" step.\n" +
				gammaProbe.getEProbeType().getName()+ " will be calibrated  before \"Mesure gamma\" step.";
	}

	public boolean isAvailable(String comPort) throws Exception {

		return isOpen;
	}

	public boolean open(String comPort) throws Exception {
		return true;
	}

	public Point3f readXYZ() throws Exception {
		return selectedProbe.readXYZ();
	}

	public boolean isOpen() {
		return isOpen;
	}

	public String getSerialInfo() {
		return gammaProbe.getEProbeType() + " : " + gammaProbe.getSerialInfo()
				+ "\n" + colorProbe.getEProbeType() + " : "
				+ colorProbe.getSerialInfo();
	}

	public boolean isSpecificCalibrationRequired() {
		return colorProbe.isSpecificCalibrationRequired()
				|| gammaProbe.isSpecificCalibrationRequired();
	}

}
