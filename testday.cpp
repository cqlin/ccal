#include <iostream>
#include <iomanip>
#include <cmath>
#include "novas.h"

int main() {
    double jd2 = 2461075.667884;
    
    std::cout << std::fixed << std::setprecision(0);
    std::cout << "(int)round(" << jd2 << ") = " << (int)round(jd2) << std::endl;
    
    return 0;
}
