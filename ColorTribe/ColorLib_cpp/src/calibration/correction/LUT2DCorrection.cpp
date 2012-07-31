#include "LUT2DCorrection.h"
#include "Point2D.h"
#include "BinarySearchCurve.h"
#include "../../exception/BinarySearchCurveException.h"
#include "../../trivialLogger/tri_logger.hpp"

#include <cmath>
#include <cstdlib>
#include <cstring>
#include <sstream>
#include <cstdio>

using namespace std;

LUT2DCorrection::~LUT2DCorrection() {
	if (_lut != NULL)
		delete[] _lut;
}

LUT2DCorrection::LUT2DCorrection(const string path, unsigned int rampSize)
		throw (CannotFindLutException) :
	_lut(NULL), _maxValue(_8BITS_LUT), _rampSize(rampSize), _gamma(1.0f),
			_redPL(0.0f), _greenPL(0.0f), _bluePL(0.0f) {
	try {
		loadLUT(path);
	}
	catch (CannotFindLutException e) {
		throw e;
	}
}

LUT2DCorrection::LUT2DCorrection(const LUT2DCorrection & lut) {
	_rampSize = lut._rampSize;
	_maxValue = lut._maxValue;
	_gamma = lut._gamma;
	_redPL = lut._redPL;
	_greenPL = lut._greenPL;
	_bluePL = lut._bluePL;
	_lut = new unsigned short[_rampSize * 3];

	for (unsigned int i = 0; i < _rampSize * 3; i++) {
		_lut[i] = lut._lut[i];
	}

}

LUT2DCorrection& LUT2DCorrection::operator=(const LUT2DCorrection & lut) {
	_rampSize = lut._rampSize;
	_maxValue = lut._maxValue;
	_gamma = lut._gamma;
	_redPL = lut._redPL;
	_greenPL = lut._greenPL;
	_bluePL = lut._bluePL;
	if (_lut != NULL)
		delete[] _lut;
	_lut = new unsigned short[_rampSize * 3];
	for (unsigned int i = 0; i < _rampSize * 3; i++) {
		_lut[i] = lut._lut[i];
	}
	return *this;
}

LUT2DCorrection::LUT2DCorrection(const unsigned short* const ramp,
		unsigned int rampSize) :
	_lut(NULL), _maxValue(_8BITS_LUT), _rampSize(rampSize), _gamma(1.0f),
			_redPL(0.0f), _greenPL(0.0f), _bluePL(0.0f) {
	_lut = new unsigned short[_rampSize * 3];
	unsigned short max = 0;
	for (unsigned int i = 0; i < _rampSize * 3; i++) {
		_lut[i] = ramp[i];
		if (_lut[i] > max)
			max = _lut[i];
	}

	if (max < 256) {
		_maxValue = _8BITS_LUT;

	} else if (max < 1024) {
		_maxValue = _10BITS_LUT;

	} else if (max < 4096) {
		_maxValue = _12BITS_LUT;

	} else {
		_maxValue = _16BITS_LUT;
	}
}

LUT2DCorrection::LUT2DCorrection(const vector<unsigned short> &ramp,
		unsigned int rampSize) :
	_lut(NULL), _maxValue(_16BITS_LUT), _rampSize(rampSize), _gamma(1.0f),
			_redPL(0.0f), _greenPL(0.0f), _bluePL(0.0f) {
	_lut = new unsigned short[_rampSize * 3];
	unsigned short max = 0;
	for (unsigned int i = 0; i < _rampSize * 3; i++) {
		_lut[i] = ramp.at(i);
		if (_lut[i] > max)
			max = _lut[i];
	}

	if (max < 256) {
		_maxValue = _8BITS_LUT;

	} else if (max < 1024) {
		_maxValue = _10BITS_LUT;

	} else if (max < 4096) {
		_maxValue = _12BITS_LUT;

	} else {
		_maxValue = _16BITS_LUT;
	}
}

void LUT2DCorrection::loadLUT(const string path) throw (CannotFindLutException) {
	FILE *pf;
	pf = fopen(path.c_str(), "r");
	if (pf) {
		_lut = new unsigned short[_rampSize * 3];
		unsigned short * ptr = _lut;
		int tmp, r, g, b;
		unsigned int index = 0;
		unsigned short max = 0;
		while ((fscanf(pf, "%d %d %d %d", &tmp, &r, &g, &b) == 4) && (index
				< _rampSize)) {
			*ptr = r;
			++ptr;
			*ptr = g;
			++ptr;
			*ptr = b;
			++ptr;
			if (r > max)
				max = r;
			if (g > max)
				max = g;
			if (b > max)
				max = b;
			index++;
		}
		fclose(pf);

		if (max < 256) {
			_maxValue = _8BITS_LUT;

		} else if (max < 1024) {
			_maxValue = _10BITS_LUT;

		} else if (max < 4096) {
			_maxValue = _12BITS_LUT;

		} else {
			_maxValue = _16BITS_LUT;
		}
	} else {
		throw CannotFindLutException(path);
	}
}
void LUT2DCorrection::dump16BitsLut() const {
	unsigned short*lut = get16BitsLUT();
	for (unsigned int i = 0; i < _rampSize * 3; i += 3) {
		std::cout << i / 3 << " " << lut[i] << " " << lut[i + 1] << " "
				<< lut[i + 2] << std::endl;
	}
	delete  []lut;
}

unsigned short* LUT2DCorrection::applyPrinterLights(int nr, int nv, int nb,
		const unsigned short*lut) const{
	unsigned short*newLut = new unsigned short[_rampSize * 3];
	unsigned int j;
	// Rouge
	if (nr != 0) {
		nr = -nr;
		for (unsigned int i = 0; i < _rampSize; i++, nr++) {

			if (nr >= (int) _rampSize)
				j = _rampSize - 1;
			else if (nr < 0)
				j = 0;
			else
				j = nr;
			newLut[i * 3] = lut[j * 3];
		}
	} else {
		for (unsigned int i = 0; i < _rampSize; i++) {
			newLut[i * 3] = lut[i * 3];
		}
	}
	//green
	if (nv != 0) {
		nv = -nv;
		for (unsigned int i = 0; i < _rampSize; i++, nv++) {
			if (nv >= (int) _rampSize)
				j = _rampSize - 1;
			else if (nv < 0)
				j = 0;
			else
				j = nv;
			newLut[i * 3 + 1] = lut[j * 3 + 1];
		}
	} else {
		for (unsigned int i = 0; i < _rampSize; i++) {
			newLut[i * 3 + 1] = lut[i * 3 + 1];
		}
	}

	// bleu
	if (nb != 0) {
		nb = -nb;
		for (unsigned int i = 0; i < _rampSize; i++, nb++) {
			if (nb >= (int)_rampSize)
				j = _rampSize - 1;
			else if (nb < 0)
				j = 0;
			else
				j = nb;

			newLut[i * 3 + 2] = lut[j * 3 + 2];
		}
	} else {
		for (unsigned int i = 0; i < _rampSize; i++) {
			newLut[i * 3 + 2] = lut[i * 3 + 2];
		}
	}
	return newLut;
}

unsigned short* LUT2DCorrection::get16BitsLUT() const {
	unsigned short*lut = new unsigned short[_rampSize * 3];
	if (_maxValue != _16BITS_LUT)
		for (unsigned int i = 0; i < _rampSize * 3; i++) {
			int tmp = (int) (_lut[i] * (float) _16BITS_LUT / (float) _maxValue
					+ .5);
			lut[i] = tmp > 65535 ? 65535 : (unsigned short) tmp;
		}
	else
		memcpy(lut, _lut, _rampSize * 3 * sizeof(unsigned short));
	if (_gamma != 1.0) {
		double gamValue = 1.0 / _gamma;
		for (unsigned int i = 0; i < _rampSize * 3; i++) {
			lut[i] = (int) (pow((_lut[i] / (double) _16BITS_LUT), gamValue)
					* (float) _16BITS_LUT);
		}
	}

	if(_redPL !=0 || _greenPL != 0 || _bluePL != 0){
		unsigned short* newLut = applyPrinterLights((int)_redPL, (int)_greenPL, (int)_bluePL,lut);
		delete []lut;
		return newLut;
	}else
	return lut;
}

unsigned short* LUT2DCorrection::get16BitsLUT(const unsigned int &cardRampSize) const {
	unsigned short*lut = get16BitsLUT();
	if (_rampSize == cardRampSize)
		return lut;
	vector<Point2D> rValues;
	vector<Point2D> bValues;
	vector<Point2D> gValues;
	//TODO add gamma stuff
	for (unsigned int i = 0; i < _rampSize; i++) {
		rValues.push_back(Point2D(i * (cardRampSize - 1) / (_rampSize - 1),
				lut[i * 3]));
		gValues.push_back(Point2D(i * (cardRampSize - 1) / (_rampSize - 1),
				lut[i * 3 + 1]));
		bValues.push_back(Point2D(i * (cardRampSize - 1) / (_rampSize - 1),
				lut[i * 3 + 2]));
	}

	delete []lut;

	unsigned short*resizedlut = new unsigned short[cardRampSize * 3];
	BinarySearchCurve rCurve(rValues);
	BinarySearchCurve gCurve(gValues);
	BinarySearchCurve bCurve(bValues);

	//interpolation
	try {
		for (unsigned int i = 0; i < cardRampSize; i++) {
			resizedlut[i * 3] = (int) rCurve.getValue(i);
			resizedlut[i * 3 + 1] = (int) gCurve.getValue(i);
			resizedlut[i * 3 + 2] = (int) bCurve.getValue(i);
		}
	}
	catch (BinarySearchCurveException bsce) {
		ostringstream os;
		os << bsce.what() << endl;
		os << "ColorRamDac exit." << endl;
		TRI_MSG_STR(os.str());
		exit(1);
	}

	return resizedlut;
}

LUT2DCorrection* LUT2DCorrection::getCombinedLUTCorrection(
		const LUT2DCorrection * customLut, const unsigned int &cardRampSize) const {
	unsigned short*lut = get16BitsLUT(cardRampSize);
	unsigned short*custLut = customLut->get16BitsLUT(cardRampSize);
	for (unsigned int i = 0; i < cardRampSize; i++) {
		int redIndex = (int) ((lut[i * 3] / 65535.0) * (cardRampSize - 1));
		int greenIndex =
				(int) ((lut[i * 3 + 1] / 65535.0 * (cardRampSize - 1)));
		int blueIndex = (int) ((lut[i * 3 + 2] / 65535.0 * (cardRampSize - 1)));
		lut[i * 3] = custLut[redIndex * 3];
		lut[i * 3 + 1] = custLut[greenIndex * 3 + 1];
		lut[i * 3 + 2] = custLut[blueIndex * 3 + 2];
	}
	LUT2DCorrection * toreturn = new LUT2DCorrection(lut, cardRampSize);
	delete []custLut;
	delete []lut;
	return toreturn;
}

LUT2DCorrection* LUT2DCorrection::getCombinedLUTCorrection(
		const LUT2DCorrection * customLut) const {
	unsigned short*lut = get16BitsLUT();
	unsigned short*custLut = customLut->get16BitsLUT();
	for (unsigned int i = 0; i < _rampSize; i++) {
		int redIndex = (int) ((lut[i * 3] / 65535.0) * (_rampSize - 1));
		int greenIndex = (int) ((lut[i * 3 + 1] / 65535.0 * (_rampSize - 1)));
		int blueIndex = (int) ((lut[i * 3 + 2] / 65535.0 * (_rampSize - 1)));
		lut[i * 3] = custLut[redIndex * 3];
		lut[i * 3 + 1] = custLut[greenIndex * 3 + 1];
		lut[i * 3 + 2] = custLut[blueIndex * 3 + 2];
	}
	delete []custLut;
	LUT2DCorrection * toreturn = new LUT2DCorrection(lut, _rampSize);
	delete []lut;
	return toreturn;
}

