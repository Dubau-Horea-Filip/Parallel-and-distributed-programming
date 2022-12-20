#include <iostream>
#include <random>
#include <chrono>
#include <thread>


#define POLYNOMIAL_ONE_SIZE 50000
#define POLYNOMIAL_TWO_SIZE 50000
#define RESULT_POLYNOMIAL_SIZE (POLYNOMIAL_ONE_SIZE + POLYNOMIAL_TWO_SIZE - 1)
#define RANDOM_NUMBER_MIN 1
#define RANDOM_NUMBER_MAX 5
#define NUMBER_OF_THREADS 12


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
    for (long long index : polynomialOne) {
        std::cout << index << " ";
    }
    std::cout << std::endl;
    std::cout << "Polynomial two" << std::endl;
    for (long long index : polynomialTwo) {
        std::cout << index << " ";
    }
    std::cout << std::endl;
}

void doTask(int threadId) {
    int resultIndex = threadId;
    while (resultIndex < RESULT_POLYNOMIAL_SIZE) {
        int value = (resultIndex < POLYNOMIAL_ONE_SIZE ? resultIndex : POLYNOMIAL_ONE_SIZE - 1);
        for (int i = 0; i <= value; i++) {
            if (resultIndex - i < POLYNOMIAL_TWO_SIZE) {
                polynomialResult[resultIndex] += polynomialOne[i] * polynomialTwo[resultIndex - i];
            }
        }
        resultIndex += NUMBER_OF_THREADS;
    }
}

void computeMultiplication() {
    std::thread taskThreads[NUMBER_OF_THREADS];

    int count = 0;
    for (auto & creatorThread : taskThreads) {
        creatorThread = std::thread(doTask, count++);
    }

    for (auto & taskThread : taskThreads) {
        taskThread.join();
    }
//    doTask(1);
}

void printResultPolynomial() {
    std::cout << std::endl << "Polynomial result" << std::endl;
    for (long long index : polynomialResult) {
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
