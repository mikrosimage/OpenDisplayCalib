/*
 * HealerCalibModel.h
 *
 *  Created on: 12 juin 2009
 *      Author: mfe
 */

#ifndef HEALERCALIBMODEL_H_
#define HEALERCALIBMODEL_H_
#include <vector>
#include <LUT2DCorrection.h>

#include <QString>

class HealerCalibModel {
public:
	HealerCalibModel(const unsigned int &screenIndex, const bool &isHighPriority);
	virtual ~HealerCalibModel();
	HealerCalibModel(const HealerCalibModel & calib);
	HealerCalibModel& operator=(const HealerCalibModel & calib);

	const unsigned int getScreenIndex() const {
		return _screenIndex;
	}
	void setSize(const unsigned int &size) {
		_values.clear();
		_lutSize = size;
		_isSizeSet = true;
	}

	bool isSizeSet() {
		return _isSizeSet;
	}

	const unsigned int & getLutSize() const {
		return _lutSize;
	}

	void setShouldDisplay(bool shouldDisplay) {
	_shouldDisplay = shouldDisplay;
	}

	bool shouldDisplay() const {
		return _shouldDisplay;
	}

	void setIsHighPriority(bool isHighPriority){
		_isHighPriority = isHighPriority;
	}

	bool isHighPriority(){
		return _isHighPriority;
	}
	void addValue(const unsigned short& r, const unsigned short& g,
			const unsigned short& b);

	const LUT2DCorrection* getCorrection() const;
	void updateCorrection( const QString & infos);

private:
	unsigned int _screenIndex;
	unsigned int _lutSize;
	bool _isSizeSet;
	bool _shouldDisplay;
	bool _isHighPriority;
	std::vector<unsigned short> _values;
	LUT2DCorrection *_corr;
};

#endif /* HEALERCALIBMODEL_H_ */
