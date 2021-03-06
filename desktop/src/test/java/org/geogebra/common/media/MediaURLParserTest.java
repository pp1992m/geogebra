package org.geogebra.common.media;

import org.geogebra.common.util.AsyncOperation;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Assert;
import org.junit.Test;

public class MediaURLParserTest {
	protected static final String MEBIS_REGEX = "https://mediathek.mebis.bayern.de/\\?doc=provideVideo&identifier=[BYWS\\-0-9]+&type=video(&)?(#t=[0-9,]+)?";
	private static AsyncOperation<VideoURL> INVALID = new AsyncOperation<VideoURL>() {

		@Override
		public void callback(VideoURL obj) {
			Assert.assertFalse(obj.isValid());
		}
	};

	private static AsyncOperation<VideoURL> validYT(final String id) {
		return valid(MediaFormat.VIDEO_YOUTUBE, id);
	}

	private static AsyncOperation<VideoURL> validMP4() {
		return valid(MediaFormat.VIDEO_HTML5, null);
	}

	private static AsyncOperation<VideoURL> validMebis() {
		return valid(MediaFormat.VIDEO_MEBIS, null);
	}

	private static AsyncOperation<VideoURL> valid(final MediaFormat fmt,
			final String id) {
		return new AsyncOperation<VideoURL>() {
			@Override
			public void callback(VideoURL obj) {
				Assert.assertTrue(obj.isValid());
				Assert.assertEquals(fmt, obj.getFormat());
				if (fmt == MediaFormat.VIDEO_YOUTUBE) {
					Assert.assertEquals(id,
						MediaURLParser.getYouTubeId(obj.getUrl()));
				}
				if (fmt == MediaFormat.VIDEO_MEBIS) {
					Assert.assertThat(obj.getUrl(),
							new TypeSafeMatcher<String>() {

								@Override
								public void describeTo(
										Description description) {
									description.appendText("Valid Mebis URL");
								}

								@Override
								public boolean matchesSafely(String item) {
									return item.matches(MEBIS_REGEX);
								}
							});
				}
			}
		};
	}

	@Test
	public void checkYoutubeUrls() {
		MediaURLParser.checkVideo("https://youtu.be/bdRUiXUrYIs",
				validYT("bdRUiXUrYIs"));
		MediaURLParser.checkVideo(
				"https://www.youtube.com/watch?v=bdRUiXUrYIs&feature=youtu.be",
				validYT("bdRUiXUrYIs"));
		MediaURLParser.checkVideo(
				"https://www.youtube.com/watch?spam&v=bdRUiXUrYIs&feature=youtu.be&spam",
				validYT("bdRUiXUrYIs"));
		MediaURLParser.checkVideo("https://youtu.be/", INVALID);
		MediaURLParser.checkVideo("https://youtu.be/?& &&", INVALID);
		MediaURLParser.checkVideo("https://youtube.com/bdRUiXUrYIs", INVALID);
	}

	@Test
	public void checkMp4Urls() {
		MediaURLParser.checkVideo("https://www.w3schools.com/htmL/mov_bbb.mp4",
				validMP4());
		MediaURLParser.checkVideo("file.mp4", validMP4());
		MediaURLParser.checkVideo("file.mp5", INVALID);
		MediaURLParser.checkVideo("https://example.com/file.mp4", validMP4());
		MediaURLParser.checkVideo("https://example.com/file.mp5", INVALID);
	}

	@Test
	public void checkMebisUrls() {
		MediaURLParser.checkVideo(
				"https://mediathek.mebis.bayern.de/?doc=embeddedObject&id=BWS-04985070&type=video&start=178&title=Wetter",
				validMebis());
		MediaURLParser.checkVideo(
				"https://mediathek.mebis.bayern.de/?doc=provideVideo&identifier=BWS-04985070&type=video&start=0&title=Wetter&file=default.mp4",
				validMebis());
		MediaURLParser.checkVideo(
				"https://mediathek.mebis.bayern.de/?doc=record&identifier=BWS-04985070",
				validMebis());
		MediaURLParser.checkVideo(
				"https://mediathek.mebis.bayern.de/?doc=record&identifier=BY-04985070",
				validMebis());
		MediaURLParser.checkVideo(
				"https://mediathek.mebis.bayern.de/?doc=provideVideo&identifier=BY-00072140&type=video&#t=60,120",
				validMebis());
		MediaURLParser.checkVideo(
				"https://mediathek.mebis.bayern.de/?doc=provideVideo&identifier=BY-00072140&type=video#t=60,120",
				validMebis());
		MediaURLParser.checkVideo(
				"https://mediathek.mebis.bayern.de/index.php?doc=provideVideo&identifier=BWS-04980092&type=video&start=0&title=Das Eichhornchen&file=default.mp4&restorePosted",
				validMebis());
		// missing id
		MediaURLParser.checkVideo(
				"https://mediathek.mebis.bayern.de/?doc=provideVideo&v=BY-00072140&type=video&#t=60,120",
				INVALID);
		// wrong host
		MediaURLParser.checkVideo(
				"https://mediathek.bayern.de/?doc=provideVideo&identifier=BY-00072140&type=video&#t=60,120",
				INVALID);
		MediaURLParser.checkVideo(
				"https://mediathek.mebis.bayern.de/?identifier=BY-00072140",
				INVALID);
		MediaURLParser.checkVideo("https://mediathek.mebis.bayern.de/?&&f&",
				INVALID);

	}
}
