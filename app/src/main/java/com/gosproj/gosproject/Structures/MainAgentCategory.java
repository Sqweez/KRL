package com.gosproj.gosproject.Structures;

import java.util.ArrayList;

public class MainAgentCategory
{
    public String name;
    public ArrayList<Agent> agents;

    public MainAgentCategory (String name, ArrayList<Agent> agents)
    {
        this.name = name;
        this.agents = agents;
    }
}
