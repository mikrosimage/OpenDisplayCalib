#include "CannotSetLutException.h"
using namespace std;
#include <iostream>

CannotSetLutException::CannotSetLutException(std::string message)
{
	_message = "Set LUT error : ";
	_message.append(message);
}

CannotSetLutException::~CannotSetLutException() throw()
{
}

const string CannotSetLutException::what() const throw()
{
  return _message;
}
