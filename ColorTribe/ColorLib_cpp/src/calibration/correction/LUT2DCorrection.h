#ifndef LUT2DCORRECTION_H_
#define LUT2DCORRECTION_H_
#include <iostream>
#include "../../exception/CannotFindLutException.h"
#include <vector>
class LUT2DCorrection {

private:


	unsigned short* _lut;

	enum eLutType { _8BITS_LUT=255, _10BITS_LUT=1023, _12BITS_LUT=4095, _16BITS_LUT=65535 };
	eLutType _maxValue;

	LUT2DCorrection() {}
	void loadLUT(const std::string path) throw (CannotFindLutException);
	unsigned int _rampSize;
	float _gamma;
	float _redPL;
	float _greenPL;
	float _bluePL;

public:
	LUT2DCorrection(const LUT2DCorrection &);
	unsigned short* applyPrinterLights(int nr, int nv, int nb,const unsigned short*lut) const ;
	LUT2DCorrection& operator=(const LUT2DCorrection &);
	unsigned int getRampSize ()const { return _rampSize; }
	LUT2DCorrection(const std::string path, unsigned int rampSize) throw (CannotFindLutException);
	LUT2DCorrection(const unsigned short* const ramp, unsigned int rampSize);
	LUT2DCorrection(const std::vector<unsigned short> &_ramp,unsigned int rampSize);
	void setGamma(float gamma) {_gamma = gamma;}
	const float& getGamma() const {return _gamma;}
	void setRedPL(float value) {_redPL = value;}
	void setGreenPL(float value) {_greenPL = value;}
	void setBluePL(float value) {_bluePL = value;}
	const float& getRedPL() const {return _redPL;}
	const float& getGreenPL() const {return _greenPL;}
	const float& getBluePL() const {return _bluePL;}
	virtual ~LUT2DCorrection();
	unsigned short* get16BitsLUT() const;
	unsigned short* get16BitsLUT(const unsigned int &cardRampSize) const;
	LUT2DCorrection* getCombinedLUTCorrection(const LUT2DCorrection *,const unsigned int &cardRampSize) const;
	LUT2DCorrection* getCombinedLUTCorrection(const LUT2DCorrection *) const;
	void dump16BitsLut() const;


};

#endif /*LUT2DCORRECTION_H_*/
