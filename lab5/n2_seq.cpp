#include <iostream>
#include <random>
#include <iomanip>
#include <chrono>


#define POLYNOMIAL_ONE_SIZE 50000
#define POLYNOMIAL_TWO_SIZE 50000
#define RESULT_POLYNOMIAL_SIZE (POLYNOMIAL_ONE_SIZE + POLYNOMIAL_TWO_SIZE - 1)
#define RANDOM_NUMBER_MIN 1
#define RANDOM_NUMBER_MAX 5


long long polynomialOne[POLYNOMIAL_ONE_SIZE];
long long polynomialTwo[POLYNOMIAL_TWO_SIZE];
long long polynomialResult[RESULT_POLYNOMIAL_SIZE];


int generateRandomNumberInRange(int min, int max) {
    std::random_device rd;
    std::mt19937 mt(rd());
    std::uniform_int_distribution<int> distribution(min, max);
    return distribution(mt);
}

void generateRandomPolynomials() {
    int index = 0;
    int number;
    while (index < POLYNOMIAL_ONE_SIZE && index < POLYNOMIAL_TWO_SIZE) {
        number = generateRandomNumberInRange(RANDOM_NUMBER_MIN, RANDOM_NUMBER_MAX);
        polynomialOne[index] = number;
        number = generateRandomNumberInRange(RANDOM_NUMBER_MIN, RANDOM_NUMBER_MAX);
        polynomialTwo[index] = number;
        index++;
    }
    while (index < POLYNOMIAL_ONE_SIZE) {
        number = generateRandomNumberInRange(RANDOM_NUMBER_MIN, RANDOM_NUMBER_MAX);
        polynomialOne[index] = number;
        index++;
    }
    while (index < POLYNOMIAL_TWO_SIZE) {
        number = generateRandomNumberInRange(RANDOM_NUMBER_MIN, RANDOM_NUMBER_MAX);
        polynomialTwo[index] = number;
        index++;
    }
}

void printPolynomials() {
    std::cout << "Polynomial one" << std::endl;
    for (int index : polynomialOne) {
        std::cout << index << " ";
    }
    std::cout << std::endl;
    std::cout << "Polynomial two" << std::endl;
    for (int index : polynomialTwo) {
        std::cout << index << " ";
    }
}

void computeMultiplication() {
    for (int i = 0; i < POLYNOMIAL_ONE_SIZE; i++) {
        for (int j = 0; j < POLYNOMIAL_TWO_SIZE; j++) {
            polynomialResult[i + j] += polynomialOne[i] * polynomialTwo[j];
        }
    }
}

void printResultPolynomial() {
    std::cout << std::endl << "Polynomial result" << std::endl;
    for (int index : polynomialResult) {
        std::cout << index << " ";
    }
    std::cout << std::endl;
}

int main() {
    generateRandomPolynomials();
//    printPolynomials();

    auto start = std::chrono::high_resolution_clock::now();
    computeMultiplication();
    auto stop = std::chrono::high_resolution_clock::now();

    auto duration = std::chrono::duration_cast<std::chrono::milliseconds>(stop - start);
    std::cout << std::endl << "Time taken: " << duration.count() << std::endl;
//    printResultPolynomial();
    return 0;
}
