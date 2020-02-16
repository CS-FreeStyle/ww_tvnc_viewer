package com.turbovnc.vncviewer;

import java.awt.Graphics;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameGrabber.Exception;
import org.bytedeco.javacv.Java2DFrameConverter;

public class RtspRunnable implements Runnable {

	private static enum TimeoutOption {
		/**
		 * Depends on protocol (FTP, HTTP, RTMP, SMB, SSH, TCP, UDP, or UNIX).
		 *
		 * http://ffmpeg.org/ffmpeg-all.html
		 */
		TIMEOUT,
		/**
		 * Protocols
		 *
		 * Maximum time to wait for (network) read/write operations to complete, in
		 * microseconds.
		 *
		 * http://ffmpeg.org/ffmpeg-all.html#Protocols
		 */
		RW_TIMEOUT,
		/**
		 * Protocols -> RTSP
		 *
		 * Set socket TCP I/O timeout in microseconds.
		 *
		 * http://ffmpeg.org/ffmpeg-all.html#rtsp
		 */
		STIMEOUT;

		public String getKey() {
			return toString().toLowerCase();
		}
	}

	private static final String SOURCE_RTSP = "rtsp://192.168.8.130/live70";
	private static final int TIMEOUT = 10; // In seconds.

	FFmpegFrameGrabber grabber;
	Frame frame;
	Java2DFrameConverter converter;
	Graphics graphic;
	DesktopWindow desktop;
	boolean running;

	public RtspRunnable(DesktopWindow _desktop) {

		desktop = _desktop;
		converter = new Java2DFrameConverter();
		grabber = new FFmpegFrameGrabber(SOURCE_RTSP);
		grabber.setOption(TimeoutOption.STIMEOUT.getKey(), String.valueOf(TIMEOUT * 1000000)); // In microseconds.
		running = true;

	}

	@Override
	public void run() {
		try {
			Thread.sleep(2000);
			grabber.start();
			while (running && (frame = grabber.grab()) != null) {
				desktop.BImage = converter.convert(frame);
				desktop.repaint();
			}
			grabber.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public synchronized void stop() {
		running = false;
	}
}
