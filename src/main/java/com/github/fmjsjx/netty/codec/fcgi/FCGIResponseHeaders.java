package com.github.fmjsjx.netty.codec.fcgi;

import java.util.Objects;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.DefaultHeaders;
import io.netty.handler.codec.ValueConverter;
import io.netty.util.ByteProcessor;
import io.netty.util.CharsetUtil;
import io.netty.util.HashingStrategy;
import io.netty.util.internal.PlatformDependent;

public final class FCGIResponseHeaders extends DefaultHeaders<String, String, FCGIResponseHeaders> {

	public FCGIResponseHeaders(ByteBuf stdoutContent) {
		this();
		parseHeaders(stdoutContent);
	}

	private FCGIResponseHeaders() {
		super(CASE_INSENSITIVE_HASHER, StringValueConverter.INSTANCE, NON_NULL);
	}

	public void parseHeaders(ByteBuf data) {
		for (;;) {
			int begin = data.readerIndex();
			int i = data.forEachByte(begin, data.readableBytes(), ByteProcessor.FIND_LF);
			if (i == -1) {
				break;
			}
			if (data.getByte(i - 1) == '\r') {
				ByteBuf header = data.readSlice(i - data.readerIndex() - 1);
				data.skipBytes(2);
				if (header.readableBytes() == 0) {
					break;
				} else {
					int j = header.forEachByte(b -> b != ':');
					String name = header.readCharSequence(j - header.readerIndex(), CharsetUtil.UTF_8).toString();
					header.skipBytes(1);
					int k = header.forEachByte(b -> b == ' ');
					if (k != -1) {
						header.skipBytes(k - header.readerIndex());
					}
					String value = header.readCharSequence(header.readableBytes(), CharsetUtil.UTF_8).toString();
					add(name, value);
				}
			} else {
				begin = i + 1;
			}
		}
	}

	private static final HashingStrategy<String> CASE_INSENSITIVE_HASHER = new HashingStrategy<String>() {
		@Override
		public int hashCode(String o) {
			return PlatformDependent.hashCodeAscii(o);
		}

		@Override
		public boolean equals(String a, String b) {
			return a.equalsIgnoreCase(b);
		}
	};

	private static final NameValidator<String> NON_NULL = Objects::requireNonNull;

	private static final class StringValueConverter implements ValueConverter<String> {

		private static final StringValueConverter INSTANCE = new StringValueConverter();

		@Override
		public String convertObject(Object value) {
			return value.toString();
		}

		@Override
		public String convertBoolean(boolean value) {
			return Boolean.toString(value);
		}

		@Override
		public boolean convertToBoolean(String value) {
			return Boolean.parseBoolean(value);
		}

		@Override
		public String convertByte(byte value) {
			return convertInt(value);
		}

		@Override
		public byte convertToByte(String value) {
			return Byte.parseByte(value);
		}

		@Override
		public String convertChar(char value) {
			return Character.toString(value);
		}

		@Override
		public char convertToChar(String value) {
			return value.charAt(0);
		}

		@Override
		public String convertShort(short value) {
			return convertInt(value);
		}

		@Override
		public short convertToShort(String value) {
			return Short.parseShort(value);
		}

		@Override
		public String convertInt(int value) {
			return Integer.toString(value);
		}

		@Override
		public int convertToInt(String value) {
			return Integer.parseInt(value);
		}

		@Override
		public String convertLong(long value) {
			return Long.toString(value);
		}

		@Override
		public long convertToLong(String value) {
			return Long.parseLong(value);
		}

		@Override
		public String convertTimeMillis(long value) {
			return convertLong(value);
		}

		@Override
		public long convertToTimeMillis(String value) {
			return convertToLong(value);
		}

		@Override
		public String convertFloat(float value) {
			return convertDouble(value);
		}

		@Override
		public float convertToFloat(String value) {
			return Float.parseFloat(value);
		}

		@Override
		public String convertDouble(double value) {
			return Double.toString(value);
		}

		@Override
		public double convertToDouble(String value) {
			return Double.parseDouble(value);
		}

	}
}
