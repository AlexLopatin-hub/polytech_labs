package org.example.internal.agent;

public class Agent {
    public Agent(State s, Policy policy) {
        this.state = new State(s.r, s.c);

        this.policy = policy;
    }

    private final State state;

    public Policy policy;

    public State getState() { return this.state; }

    public Action selectAction(boolean[] allowedActions) {
        return this.policy.selectAction(this.state, allowedActions);
    }

    public void setState(State newState) {
        this.state.r = newState.r;
        this.state.c = newState.c;
    }

    public void updatePolicy(State oldState, State newState, int actionID, double reward) {
        this.policy.update(oldState, newState, actionID, reward);
    }
}
