#include <iostream>
#include <cmath>
#include <iomanip>

int main() {
    // Test C++ fmod with the same values
    double testNeg = -1366704.1794153184;
    double modulo = 1296000.0;

    double result = fmod(testNeg, modulo);

    std::cout << std::fixed << std::setprecision(10);
    std::cout << "C++ fmod(" << testNeg << ", " << modulo << ") = " << result << std::endl;

    // Java equivalent
    double javaResult = testNeg - floor(testNeg / modulo) * modulo;
    std::cout << "Java fmod(" << testNeg << ", " << modulo << ") = " << javaResult << std::endl;

    std::cout << "Difference: " << (result - javaResult) << std::endl;

    return 0;
}
