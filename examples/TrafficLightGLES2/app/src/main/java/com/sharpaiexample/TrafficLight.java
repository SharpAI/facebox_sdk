package com.sharpaiexample;

public class TrafficLight {
	//Enums:
	public enum State {
		ST_OFF,
		ST_BLINKING,
		ST_RED,
		ST_GREEN,
	}
	
	public enum Color {
		C_RED,
		C_REDYELLOW,
		C_GREEN,
		C_YELLOW,
		C_OFF
	}
	
	//Member variables:
	private Color activeColor = Color.C_OFF;
	private State activeState = State.ST_OFF;
	private State targetState = State.ST_OFF;
	private double actualTime = 0.0;
	private boolean automatic = false;

	//Member functions:
	public void SetOff() {
		targetState = State.ST_OFF;
	}
	
	public void SetBlinking() {
		targetState = State.ST_BLINKING;
	}

	public void SetRed() {
		targetState = State.ST_RED;
	}

	public void SetGreen() {
		targetState = State.ST_GREEN;
	}
	
	public void SetAutomatic(boolean _automatic) {
		automatic = _automatic;
	}
	
	public void SetNextTargetState() {
		if (activeState == targetState)
		{
			switch (targetState) {
			case ST_OFF: targetState = State.ST_OFF; break;
			case ST_BLINKING: targetState = State.ST_BLINKING; break;
			case ST_RED: targetState = State.ST_GREEN; break;
			case ST_GREEN: targetState = State.ST_RED; break;
			default: break;
			}
		}
	}
	
	public Color GetActiveColor() {
		return activeColor;
	}
	
	private void Blinking() {
		activeColor = (activeColor == Color.C_YELLOW) ? Color.C_OFF : Color.C_YELLOW;
		actualTime = 0.0f;
	}
	
	private double GetTimeInState() {
		//Active color time in active state
		if (activeState == State.ST_BLINKING) return 0.5;
		if (activeColor == Color.C_GREEN) return 1.0;
		if (activeColor == Color.C_RED) return 1.0;
		if (activeColor == Color.C_REDYELLOW) return 2.0;
		if (activeColor == Color.C_YELLOW) return 2.0;
		return 1.0;
	}
	
	public void Run(double timeDelta) {
		actualTime += timeDelta;
		
		if (automatic && (activeState == targetState)) {
			switch (activeState) {
			case ST_BLINKING: break;
			case ST_OFF: targetState = State.ST_BLINKING; break;
			case ST_GREEN: targetState = State.ST_RED; break;
			case ST_RED: targetState = State.ST_GREEN; break;
			}
		}
		
		if (actualTime >= GetTimeInState()) { //Can switch color/state
			if (activeState != targetState) {
				if (activeState == State.ST_OFF || activeState == State.ST_BLINKING || targetState == State.ST_OFF || targetState == State.ST_BLINKING) {
					activeState = targetState;
					switch (activeState) {
					case ST_BLINKING: activeColor = Color.C_YELLOW; break;
					case ST_OFF: activeColor = Color.C_OFF; break;
					case ST_GREEN: activeColor = Color.C_GREEN; break;
					case ST_RED: activeColor = Color.C_RED; break;
					}
					actualTime = 0.0;
				} else if (activeState == State.ST_RED) {
					if (activeColor == Color.C_RED) {
						activeColor = Color.C_REDYELLOW;
						actualTime = 0.0;
					} else if (activeColor == Color.C_REDYELLOW) {
						activeState = State.ST_GREEN;
						activeColor = Color.C_GREEN;
						actualTime = 0.0;
					} else {
						throw new RuntimeException("Why");
					}
				}
				else if (activeState == State.ST_GREEN) {
					if (activeColor == Color.C_GREEN) {
						activeColor = Color.C_YELLOW;
						actualTime = 0.0;
					} else if (activeColor == Color.C_YELLOW) {
						activeState = State.ST_RED;
						activeColor = Color.C_RED;
						actualTime = 0.0;
					} else {
						throw new RuntimeException("Why");
					}
				}
			} else if (activeState == State.ST_BLINKING) {
				Blinking();
			}
		}
	}
	
}
