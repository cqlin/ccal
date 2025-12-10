#include <iostream>
#include <iomanip>
#include <vector>
#include "solarterm.h"

int main() {
    short year = 2026;
    double jdpws;
    std::vector<double> vterms;
    solarterm(year, jdpws, vterms);

    std::cout << std::fixed << std::setprecision(6);
    std::cout << "Solar terms for 2026:" << std::endl;
    std::cout << "LC (index 2): " << vterms[2] << " (int: " << (int)vterms[2] << ")" << std::endl;
    std::cout << "QM (index 6): " << vterms[6] << " (int: " << (int)vterms[6] << ")" << std::endl;
    std::cout << "XM (index 9): " << vterms[9] << " (int: " << (int)vterms[9] << ")" << std::endl;
    std::cout << "DS (index 13): " << vterms[13] << " (int: " << (int)vterms[13] << ")" << std::endl;
    std::cout << "QF (index 17): " << vterms[17] << " (int: " << (int)vterms[17] << ")" << std::endl;
    std::cout << "DZ (index 23): " << vterms[23] << " (int: " << (int)vterms[23] << ")" << std::endl;

    return 0;
}
