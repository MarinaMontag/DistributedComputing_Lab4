package main

import (
	"math/rand"
	"sync"
)

func main() {

}

type Node struct {
	city string
}

type Edge struct {
	price int
	cityA *Node
	cityB *Node
}

type Graph struct {
	nodes []*Node
	edges []*Edge
	lock  sync.RWMutex
}

//потік, що змінює ціну квитка;

//////////////////////////////////////////////////////////////////////////////

//потік, що видаляє і додає рейси між містами;

func (graph *Graph) AddFlight(price int, cityA *Node, cityB *Node) {
	graph.lock.Lock()
	edge1 := Edge{price, cityA, cityB}
	edge2 := Edge{price, cityB, cityA}
	graph.edges = append(graph.edges, &edge1, &edge2)
	graph.lock.Unlock()
}

func (graph *Graph) DeleteFlight(i int) {
	graph.lock.Lock()
	graph.edges = append(graph.edges[:i], graph.edges[(i+1):]...)
	graph.lock.Unlock()
}

////////////////////////////////////////////////////////////////////////////////////

//потік, що видаляє старі міста і додає нові;

func (graph *Graph) AddCity(node *Node) {
	graph.lock.Lock()
	graph.nodes = append(graph.nodes, node)
	graph.lock.Unlock()
}

func (graph *Graph) DeleteCity() {
	graph.lock.Lock()
	deletedNode := graph.deleteNode()
	if deletedNode == nil {
		defer graph.lock.Unlock()
		return
	}
	graph.deleteAdjacentEdges(deletedNode)
	graph.lock.Unlock()

}

func (graph *Graph) deleteNode() *Node {
	amountOfCities := len(graph.nodes)
	if amountOfCities == 0 {
		return nil
	}
	toDeleteIndex := rand.Intn(amountOfCities)
	toDeleteNode := graph.nodes[toDeleteIndex]
	graph.nodes = append(graph.nodes[:toDeleteIndex], graph.nodes[(toDeleteIndex+1):]...)
	return toDeleteNode
}

func (graph *Graph) deleteAdjacentEdges(node *Node) {
	for i, edge := range graph.edges {
		if edge.cityA == node || edge.cityB == node {
			graph.edges = append(graph.edges[:i], graph.edges[(i+1):]...)
		}
	}
}

////////////////////////////////////////////////////////////////////////////////
