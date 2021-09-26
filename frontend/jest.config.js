module.exports = {
	"verbose": true,
	moduleFileExtensions: ['js', 'vue'],
	transform: {
		'^.+\\.js$': 'babel-jest',
    '^.+\\.tsx?$': 'babel-jest',
    '^.+\\.jsx?$': 'babel-jest',
		'.*\\.vue$': 'vue-jest',
    "^.+\\.svg$": "<rootDir>/svgTransform.js"
	},
  transformIgnorePatterns: ['/node_modules/(?!(vue-material-design-icons)|(vue-spinner))'],
	moduleNameMapper: {
		'^@/(.*)$': '<rootDir>/src/$1',
		'^tests/(.*)$': '<rootDir>/tests/$1'
	}
};