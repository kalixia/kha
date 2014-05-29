package com.kalixia.ha.devices.weather;

/**
 *
 * Note: <a href="http://www.wunderground.com/weather/api/d/docs?d=resources/phrase-glossary">Wunderground has a list of their icons and descriptions</a>.
 */
public enum WeatherIcon {
    CLOUD('!'),
    CLOUD_SUN('"'),
    CLOUD_MOON('#'),

    RAIN('$'),
    RAIN_SUN('%'),
    RAIN_MOON('&'),

    RAIN_ALT('\''),
    RAIN_SUN_ALT('('),
    RAIN_MOON_ALT(')'),

    DOWNPOUR('*'),
    DOWNPOUR_SUN('+'),
    DOWNPOUR_MOON(','),

    DRIZZLE('-'),
    DRIZZLE_SUN('.'),
    DRIZZEL_MOON('/'),

    SLEET('0'),
    SLEET_SUN('1'),
    SLEET_MOON('2'),

    HAIL('3'),
    HAIL_SUN('4'),
    HAIL_MOON('5'),

    FLURRIES('6'),
    FLURRIES_SUN('7'),
    FLURRIES_MOON('8'),

    SNOW('9'),
    SNOW_SUN(':'),
    SNOW_MOON(';'),

    FOG('<'),
    FOG_SUN('='),
    FOG_MOON('>'),

    HAZE('?'),
    HAZE_SUN('@'),
    HAZE_MOON('A'),

    WIND('B'),
    WIND_CLOUD('C'),
    WIND_CLOUD_SUN('D'),
    WIND_CLOUD_MOON('E'),

    LIGHTNING('F'),
    LIGHTNING_SUN('G'),
    LIGHTNING_MOON('H'),

    SUN('I'),
    SUNSET('J'),
    SUNRISE('K'),
    SUN_LOW('L'),
    SUN_LOWER('M'),

    MOON('N'),
    MOON_NEW('O'),
    MOON_WAXING_CRESCENT('P'),
    MOON_WAXING_QUARTER('Q'),
    MOON_WAXING_GIBBOUS('R'),
    MOON_FULL('S'),
    MOON_WANING_GIBBOUS('T'),
    MOON_WANING_QUARTER('U'),
    MOON_WANING_CRESCENT('V'),

    SNOWFLAKE('W'),
    TORNADO('X'),

    THERMOMETER('Y'),
    THERMOMETER_LOW('Z'),
    THERMOMETER_MEDIUM_LOW('['),
    THERMOMETER_MEDIUM_HIGH('\\'),
    THERMOMETER_HIGH(']'),
    THERMOMETER_FULL('^'),
    CELSIUS('_'),
    FARENHEIT('\''),
    COMPASS('a'),
    COMPASS_NORTH('b'),
    COMPASS_EAST('c'),
    COMPASS_SOUTH('d'),
    COMPASS_WEST('e'),

    UMBRELLA('f'),
    SUN_GLASSES('g'),

    CLOUD_REFRESH('h'),
    CLOUD_UP('i'),
    CLOUD_DOWN('j');

    private final char fontCharacter;

    WeatherIcon(char fontCharacter) {
        this.fontCharacter = fontCharacter;
    }

    public char getFontCharacter() {
        return fontCharacter;
    }
}
