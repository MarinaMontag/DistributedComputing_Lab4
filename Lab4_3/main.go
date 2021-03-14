package main

import (
	"math/rand"
	"sync"
)

func main() {

}

type City struct {
	name string
}

type Flight struct {
	price     int
	direction *City
}

type Airport struct {
	cities  []*City
	flights map[*City][]*Flight
	lock    sync.RWMutex
}

//потік, що змінює ціну квитка;

func (graph *Airport) changePrice() {
	newPrice := (rand.Intn(20) + 1) * 100
	graph.lock.RLock()
	A := rand.Intn(len(graph.cities))
	cityA := graph.cities[A]
	B := rand.Intn(len(graph.flights[cityA]))
	cityB := graph.flights[cityA][B].direction
	graph.lock.Lock()
	graph.flights[cityA][B].price = newPrice
	for i, flight := range graph.flights[cityB] {
		if flight.direction == cityA {
			graph.flights[cityB][i].price = newPrice
			break
		}
	}
	graph.lock.Unlock()
	graph.lock.RUnlock()
}

//////////////////////////////////////////////////////////////////////////////

//потік, що видаляє і додає рейси між містами;

func (graph *Airport) AddFlight(price int, cityA *City, cityB *City) {
	graph.lock.Lock()
	flight1 := Flight{price, cityA}
	flight2 := Flight{price, cityB}
	graph.flights[cityA] = append(graph.flights[cityA], &flight2)
	graph.flights[cityB] = append(graph.flights[cityB], &flight1)
	graph.lock.Unlock()
}

func (graph *Airport) DeleteFlight(A int, B int) {
	graph.lock.Lock()
	cityA := graph.cities[A]
	cityB := graph.cities[B]
	graph.deleteDirection(cityA, cityB)
	graph.deleteDirection(cityB, cityA)
	graph.lock.Unlock()
}

func (graph *Airport) deleteDirection(cityA *City, cityB *City) {
	flightsFromA := graph.flights[cityA]
	for i, flight := range flightsFromA {
		if flight.direction == cityB {
			flightsFromA = append(flightsFromA[:i], flightsFromA[i+1:]...)
			graph.flights[cityA] = flightsFromA
			break
		}
	}
}

////////////////////////////////////////////////////////////////////////////////////

//потік, що видаляє старі міста і додає нові;

func (graph *Airport) AddCity(city *City) {
	graph.lock.Lock()
	graph.flights[city] = nil
	graph.cities = append(graph.cities, city)
	graph.lock.Unlock()
}

func (graph *Airport) DeleteCity() {
	graph.lock.Lock()
	deletedCity := graph.deleteNode()
	if deletedCity == nil {
		defer graph.lock.Unlock()
		return
	}
	graph.deleteFlightsToDeletedCity(deletedCity)
	graph.lock.Unlock()

}

func (graph *Airport) deleteNode() *City {
	amountOfCities := len(graph.cities)
	if amountOfCities == 0 {
		return nil
	}
	toDeleteIndex := rand.Intn(amountOfCities)
	toDeleteCity := graph.cities[toDeleteIndex]
	graph.cities = append(graph.cities[:toDeleteIndex], graph.cities[(toDeleteIndex+1):]...)
	return toDeleteCity
}

func (graph *Airport) deleteFlightsToDeletedCity(deletedCity *City) {
	flightsFromDeletedCity := graph.flights[deletedCity]
	delete(graph.flights, deletedCity)
	for _, flightFromDeletedCity := range flightsFromDeletedCity {
		cities := graph.flights[flightFromDeletedCity.direction]
		for i, city := range cities {
			if city.direction == deletedCity {
				cities = append(cities[:i], cities[(i+1):]...)
				graph.flights[flightFromDeletedCity.direction] = cities
				break
			}
		}
	}
}

////////////////////////////////////////////////////////////////////////////////

//потоки, що визначають чи є шлях від довільного міста А до довільного міста Б,
//і яка ціна такої поїздки (якщо прямого шляху немає, то знайти будь-який шлях з існуючих)

func (graph *Airport) searchFlight(cityA *City, cityB *City) {

}

/////////////////////////////////////////////////////////////////////////////////////
