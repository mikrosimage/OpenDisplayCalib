/*
 * BinarySearchCurveException.h
 *
 *  Created on: 11 dec. 2008
 *      Author: mfe
 */

#ifndef BINARYSEARCHCURVEEXCEPTION_H_
#define BINARYSEARCHCURVEEXCEPTION_H_

#include "ColorRamDacException.h"
#include <iostream>

class BinarySearchCurveException  : public ColorRamDacException
{
	std::string _message;
public:
	BinarySearchCurveException(std::string message);
	virtual ~BinarySearchCurveException() throw ();
	const std::string what() const throw();
};

#endif /* BINARYSEARCHCURVEEXCEPTION_H_ */
