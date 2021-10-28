package ex0.tests;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.jupiter.api.Test;

import ex0.Building;
import ex0.CallForElevator;
import ex0.algo.MyAlgo;
import ex0.simulator.Call_A;
import ex0.simulator.Simulator_A;

class MyAlgoTest {


    Building building;
    //Building b9;
    MyAlgo algo;
    //MyAlgo algoB9;

    public MyAlgoTest(){

        Simulator_A.initData(9,null);
        building = Simulator_A.getBuilding();
        //Simulator_A.initData(9,null);
        //b9 = Simulator_A.getBuilding();

        algo = new MyAlgo(building);
        //algoB9 =new algo1(b9);

    }

    @Test
    void algoNametest() {
        assertEquals("TheBestAlgoritemEver!", algo.algoName());
    }
    @Test
    void getBuildingTest() {
        assertEquals(building, algo.getBuilding());
        System.out.println(building);
    }
    
    @Test
    void allocateAnElevator() {
        CallForElevator call1 = new Call_A(1, 60, 0);
        assertEquals(0, algo.allocateAnElevator(call1));
        assertEquals(1, algo.allocateAnElevator(call1));
        CallForElevator call2 = new Call_A(5, 0, 0);
        assertEquals(3, algo.allocateAnElevator(call2));
        assertEquals(5, algo.allocateAnElevator(call2));
        CallForElevator call3 = new Call_A(60, 70, 0);
        assertEquals(6, algo.allocateAnElevator(call3));
        assertEquals(7, algo.allocateAnElevator(call3));
    }

    
    @Test
    void cmdElevator() {
        allocateAnElevator();
        
        assertEquals(1, algo.elevators[0].getState());
        assertEquals(1, algo.elevators[1].getState());
        assertEquals(-1, algo.elevators[3].getState());
        assertEquals(-1, algo.elevators[5].getState());
        assertEquals(1, algo.elevators[6].getState());
        assertEquals(1, algo.elevators[7].getState());
    }
}
