module.exports = function(grunt) {
    grunt.loadNpmTasks('grunt-browserify');
    grunt.loadNpmTasks('grunt-sass');

    grunt.initConfig({
        browserify: {
            appJs: {
                options: {
                    browserifyOptions: {
                        paths: ['./src/main/js']
                    },
                    transform: [
                        ['babelify', {presets: ['react', 'es2015']}]
                    ]
                },
                files: {
                    'src/main/resources/static/built/bundle.js': 'src/main/js/main.js'
                }
            }
        },
        sass: {
            appSass: {
                options: {
                    noCache: true
                },
                files: {
                    'src/main/resources/static/built/bundle.css': 'src/main/resources/static/sass/styles.scss'
                }
            }
        }
    });

    grunt.registerTask('build', ['browserify:appJs', 'sass:appSass']);
};
