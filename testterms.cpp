#include <iostream>
#include <iomanip>
#include <vector>
#include "solarterm.h"

int main(int argc, char** argv) {
    short year = 2026;
    double jdpws;
    std::vector<double> vterms;
    
    if(argc>1)
    	year = atoi(argv[1]);

    std::cout << std::fixed << std::setprecision(16);
    std::cout << "angle " << timeangle(10000) << std::endl;
    std::cout << "year " << year << std::endl;
    
    solarterm(year, jdpws, vterms);
    std::cout << "jdpws: " << jdpws <<":" << std::endl;
    std::cout << "LC (index 2): " << vterms[2] << " (int: " << (int)vterms[2] << ")" << std::endl;
    std::cout << "LC (index 2): " << vterms[2] << " (int: " << (int)vterms[2] << ")" << std::endl;
    std::cout << "QM (index 6): " << vterms[6] << " (int: " << (int)vterms[6] << ")" << std::endl;
    std::cout << "XM (index 9): " << vterms[9] << " (int: " << (int)vterms[9] << ")" << std::endl;
    std::cout << "DS (index 13): " << vterms[13] << " (int: " << (int)vterms[13] << ")" << std::endl;
    std::cout << "QF (index 17): " << vterms[17] << " (int: " << (int)vterms[17] << ")" << std::endl;
    std::cout << "DZ (index 23): " << vterms[23] << " (int: " << (int)vterms[23] << ")" << std::endl;
    size_t length = vterms.size();
    for(int i=0;i<length;i++){
		std::cout << i <<":" << vterms[i] << std::endl;
	}

    return 0;
}
