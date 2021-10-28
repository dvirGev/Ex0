package ex0.algo;

import ex0.Building;
import ex0.CallForElevator;
import ex0.Elevator;
import ex0.simulator.Call_A;
import ex0.simulator.ElevetorCallList;
import ex0.simulator.Simulator_A;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class MyAlgoTest {


    Building building;
    //Building b9;
    MyAlgo algo;
    //MyAlgo algoB9;

    public MyAlgoTest(){

        Simulator_A.initData(2,null);
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
        CallForElevator call = new Call_A(0, 0, 1);
        assertEquals(1, algo.allocateAnElevator(call));
    }

    @Test
    void cmdElevator() {
        
    }
}