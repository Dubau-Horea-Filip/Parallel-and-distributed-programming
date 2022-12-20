#include <iostream>
#include <random>
#include <iomanip>
#include <chrono>
#include <vector>


#define POLYNOMIAL_ONE_SIZE 20
#define POLYNOMIAL_TWO_SIZE 20
#define RESULT_POLYNOMIAL_SIZE (POLYNOMIAL_ONE_SIZE + POLYNOMIAL_TWO_SIZE - 1)
#define RANDOM_NUMBER_MIN 1
#define RANDOM_NUMBER_MAX 5


std::vector<long long> polynomialOne;
std::vector<long long> polynomialTwo;
//std::vector<long long> polynomialResult(RESULT_POLYNOMIAL_SIZE, 0);

enum POLYNOMIAL_OPERATION {
    ADDITION,
    SUBTRACTION
};

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
        polynomialOne.push_back(number);
        number = generateRandomNumberInRange(RANDOM_NUMBER_MIN, RANDOM_NUMBER_MAX);
        polynomialTwo.push_back(number);
        index++;
    }
    while (index < POLYNOMIAL_ONE_SIZE) {
        number = generateRandomNumberInRange(RANDOM_NUMBER_MIN, RANDOM_NUMBER_MAX);
        polynomialOne.push_back(number);
        index++;
    }
    while (index < POLYNOMIAL_TWO_SIZE) {
        number = generateRandomNumberInRange(RANDOM_NUMBER_MIN, RANDOM_NUMBER_MAX);
        polynomialTwo.push_back(number);
        index++;
    }
}

void printPolynomials() {
    std::cout << "Polynomial one" << std::endl;
    for (long long index: polynomialOne) {
        std::cout << index << " ";
    }
    std::cout << std::endl;
    std::cout << "Polynomial two" << std::endl;
    for (long long index: polynomialTwo) {
        std::cout << index << " ";
    }
    std::cout << std::endl;
}

void polynomialOperation(
        std::vector<long long> &first_polynomial,
        std::vector<long long> &second_polynomial,
        POLYNOMIAL_OPERATION op,
        std::vector<long long> &result
) {
    int i = 0, j = 0;
    while (i < first_polynomial.size() && j < second_polynomial.size()) {
        long long value;
        switch (op) {
            case ADDITION: value = first_polynomial[i] + second_polynomial[j]; break;
            case SUBTRACTION: value = first_polynomial[i] - second_polynomial[j]; break;
        }
        result.push_back(value);
        i++;
        j++;
    }
    while (i < first_polynomial.size()) {
        result.push_back(first_polynomial[i]);
        i++;
    }
    while (j < second_polynomial.size()) {
        result.push_back(second_polynomial[j]);
        j++;
    }
}

void addPolynomialPower(
        std::vector<long long> &polynomial,
        size_t power
) {
    for (size_t index = 0; index < power; index++) {
        polynomial.push_back(0);
    }
    for (int index = polynomial.size() - power - 1; index >= 0; index--) {
        polynomial[index + power] = polynomial[index];
    }
    for (size_t index = 0; index < power; index++) {
        polynomial[index] = 0;
    }
}

void computeMultiplication(
        std::vector<long long> &firstPolynomial,
        std::vector<long long> &secondPolynomial,
        std::vector<long long> &result
) {
    if (firstPolynomial.size() < 2 && secondPolynomial.size() < 2) {
        result.push_back(firstPolynomial[0] * secondPolynomial[0]);
        return;
    }

    std::vector<long long> d1;
    std::vector<long long> d0;
    std::vector<long long> e1;
    std::vector<long long> e0;

    for (size_t index = 0; index < firstPolynomial.size() / 2; index++) {
        d0.push_back(firstPolynomial[index]);
    }
    for (size_t index = firstPolynomial.size() / 2; index < firstPolynomial.size(); index++) {
        d1.push_back(firstPolynomial[index]);
    }

    for (size_t index = 0; index < secondPolynomial.size() / 2; index++) {
        e0.push_back(secondPolynomial[index]);
    }
    for (size_t index = secondPolynomial.size() / 2; index < secondPolynomial.size(); index++) {
        e1.push_back(secondPolynomial[index]);
    }

    std::vector<long long> d1e1;
    computeMultiplication(d1, e1, d1e1);
    std::vector<long long> d0e0;
    computeMultiplication(d0, e0, d0e0);
    std::vector<long long> polSumD1D0;
    std::vector<long long> polSumE1E0;
    polynomialOperation(d1, d0, ADDITION, polSumD1D0);
    polynomialOperation(e1, e0, ADDITION, polSumE1E0);
    std::vector<long long> polMultiplication;
    computeMultiplication(
            polSumD1D0,
            polSumE1E0,
            polMultiplication
    );
    std::vector<long long> firstResult(d1e1);
    addPolynomialPower(firstResult, firstPolynomial.size() + secondPolynomial.size() - 1 - d1e1.size());

    std::vector<long long> subs1;
    polynomialOperation(polMultiplication, d1e1, SUBTRACTION, subs1);
    std::vector<long long> secondResult;
    polynomialOperation(
            subs1,
            d0e0,
            SUBTRACTION,
            secondResult
    );
    addPolynomialPower(secondResult, (firstPolynomial.size() + secondPolynomial.size() - 1 - d1e1.size())/2);

    std::vector<long long> polAddition;
    polynomialOperation(firstResult, secondResult, ADDITION, polAddition);
    polynomialOperation(polAddition, d0e0, ADDITION, result);
}

void printResultPolynomial(std::vector<long long> &polynomialResult) {
    std::cout << std::endl << "Polynomial result" << std::endl;
    for (long long index: polynomialResult) {
        std::cout << index << " ";
    }
    std::cout << std::endl;
}

int main() {
    generateRandomPolynomials();
//    polynomialOne.push_back(1);
//    polynomialOne.push_back(2);
//    polynomialOne.push_back(3);
//    polynomialOne.push_back(4);
//
//    polynomialTwo.push_back(4);
//    polynomialTwo.push_back(3);
//    polynomialTwo.push_back(2);
//    polynomialTwo.push_back(1);

//    polynomialOne.push_back(5);
//    polynomialOne.push_back(7);
//    polynomialOne.push_back(8);
//
//    polynomialTwo.push_back(6);
//    polynomialTwo.push_back(5);
//    polynomialTwo.push_back(11);
//    printPolynomials();

//    std::vector<long long> result;
//    polynomialOperation(polynomialOne, polynomialTwo, SUBTRACTION, result);
//    printResultPolynomial(result);
    auto start = std::chrono::high_resolution_clock::now();
    std::vector<long long> polynomialResult;
    computeMultiplication(polynomialOne, polynomialTwo, polynomialResult);
    auto stop = std::chrono::high_resolution_clock::now();

    auto duration = std::chrono::duration_cast<std::chrono::milliseconds>(stop - start);
    std::cout << std::endl << "Time taken: " << duration.count() << std::endl;
//    printResultPolynomial(polynomialResult);
    return 0;
}
