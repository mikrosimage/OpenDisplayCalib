/*
 * BinarySearchCurveException.cpp
 *
 *  Created on: 11 dec. 2008
 *      Author: mfe
 */

#include "BinarySearchCurveException.h"
using namespace std;
#include <iostream>

BinarySearchCurveException::BinarySearchCurveException(std::string message)
{
	_message = "Binary search curve error : ";
	_message.append(message);
}

BinarySearchCurveException::~BinarySearchCurveException() throw()
{
}

const string BinarySearchCurveException::what() const throw()
{
  return _message;
}
