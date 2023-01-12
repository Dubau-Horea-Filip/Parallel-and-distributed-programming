#include <iostream>
#include <random>
#include <thread>
#include <vector>
#include <mutex>

#define NR_NODES 20
#define NR_EDGES 300
#define THREAD_COUNT 12

int graph[NR_NODES][NR_NODES];
bool finished = false;
std::mutex mutex;


int generateRandomNumberInRange(int min, int max) {
    std::random_device rd;
    std::mt19937 mt(rd());
    std::uniform_int_distribution<int> distribution(min, max);
    return distribution(mt);
}

void generateRandomGraph() {
    int edgeCount = 0;
    while (edgeCount < NR_EDGES) {
        int firstNode = generateRandomNumberInRange(0, NR_NODES - 1);
        int secondNode = generateRandomNumberInRange(0, NR_NODES - 1);
        graph[firstNode][secondNode] = 1;
        edgeCount++;
    }
}

void generateHGraph() {
#define NR_NODES 10
#define NR_EDGES 10

    graph[0][1] = 1;
    graph[1][2] = 1;
    graph[2][3] = 1;
    graph[3][4] = 1;
    graph[4][5] = 1;
    graph[5][6] = 1;
    graph[6][7] = 1;
    graph[7][8] = 1;
    graph[8][9] = 1;
    graph[9][0] = 1;

}

void generateNOTHGraph() {
#define NR_NODES 10
#define NR_EDGES 10
    graph[0][1] = 1;
    graph[1][2] = 1;
    graph[2][3] = 1;
}

bool checkPath(const std::vector<int> &path) {
    int index = 0;
    while (index < path.size() - 2) {
        if (graph[path[index]][path[index + 1]] == 0) {
            return false;
        }
        index++;
    }
    return true;
}

void printPath(const std::vector<int> &path) {
    std::cout << "Found path: " << std::endl;
    for (auto &node: path) {
        std::cout << node << " -> ";
    }
    std::cout << std::endl;
}

void doTask() {
    int pathIndex;
    while (!finished) {
        pathIndex = 0;
        std::vector<int> path(NR_NODES);
        std::vector<int> availableNodes(NR_NODES);
        for (int index = 0; index < NR_NODES; index++)
            availableNodes[index] = index;
        while (!availableNodes.empty()) {
            int availableNodesIndex = generateRandomNumberInRange(0, availableNodes.size() - 1);
            int node = availableNodes[availableNodesIndex];
            path[pathIndex++] = node;
            std::swap(availableNodes[availableNodesIndex], availableNodes[availableNodes.size() - 1]);
            availableNodes.pop_back();
        }
//        printPath(path);
        if (checkPath(path)) {
//            std::cout << "daa" << std::endl;
            mutex.lock();
            if (finished) {
                break;
            }
            finished = true;
            printPath(path);
            mutex.unlock();
        }
    }
}

int main() {
    //generateRandomGraph();
    //generateHGraph();
    generateNOTHGraph();

    std::thread taskThreads[THREAD_COUNT];

    for (auto &creatorThread: taskThreads) {
        creatorThread = std::thread(doTask);
    }

    for (auto &taskThread: taskThreads) {
        taskThread.join();
    }

    return 0;
}
