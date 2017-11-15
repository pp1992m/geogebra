package org.geogebra.common.geogebra3D.euclidian3D.animator;

import org.geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import org.geogebra.common.geogebra3D.euclidian3D.animator.EuclidianView3DAnimator.AnimationType;

/**
 * animation for continue rotation
 *
 */
public class EuclidianView3DAnimationContinueRotation extends EuclidianView3DAnimation {

	private double animatedRotSpeed, animatedRotTimeStart, aOld, bOld;

	/**
	 * 
	 * @param view3D
	 * @param animator
	 * @param delay
	 * @param rotSpeed
	 */
	public EuclidianView3DAnimationContinueRotation(EuclidianView3D view3D, EuclidianView3DAnimator animator,
			double delay, double rotSpeed) {

		super(view3D, animator);
		double rotSpeed2 = rotSpeed;
		// if speed is too large, use max speed
		if (rotSpeed2 > 0.1) {
			rotSpeed2 = 0.1;
		} else if (rotSpeed2 < -0.1) {
			rotSpeed2 = -0.1;
		}
		view3D.getSettings().setRotSpeed(0);
		animatedRotSpeed = -rotSpeed2;
		animatedRotTimeStart = -delay;
	}

	public void setupForStart() {
		animatedRotTimeStart += view3D.getApplication().getMillisecondTime();
		aOld = view3D.getAngleA();
		bOld = view3D.getAngleB();
	}

	public AnimationType getType() {
		return AnimationType.CONTINUE_ROTATION;
	}

	public void animate() {
		double da = (view3D.getApplication().getMillisecondTime() - animatedRotTimeStart) * animatedRotSpeed;
		view3D.setRotXYinDegrees(aOld + da, bOld);
		view3D.updateRotationAndScaleMatrices();
		view3D.setGlobalMatrices();
		view3D.setViewChangedByRotate();
	}

}