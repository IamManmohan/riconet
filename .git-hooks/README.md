### Git Hooks 
* Centralized git hooks
   * pre-commit
   * pre-push 

### How to add this repo to your project as git subtree
* `git subtree add --prefix .git-hooks git@bitbucket.org:rivigotech/git-hooks.git master --squash`

### How to get updated git-hooks
* Manual
  * `git subtree pull --prefix .git-hooks git@bitbucket.org:rivigotech/git-hooks.git master --squash`
* Auto
  *  https://jenkins-v2.rivigo.com/job/GitHooks_Update/

### How to add git-hooks to your project?
* gradle

     ```
      tasks.create(name: 'gitExecutableHooks') {
            doLast {
                Runtime.getRuntime().exec("chmod -R +x .git/hooks/");
            }
       }
       task installGitHooks(type: Copy) {
           from ".git-hooks/src/hooks/pre-push/gradle/pre-push",".git-hooks/src/hooks/pre-commit/commit-msg"
            into ".git/hooks"
        }
       gitExecutableHooks.dependsOn(installGitHooks)
       build.dependsOn(gitExecutableHooks)
     ```
     
* maven

    ```
      <plugin>
        <artifactId>maven-antrun-plugin</artifactId>
        <version>${mvn.antrun.plugin.version}</version>
        <executions>
          <execution>
            <id>install</id>
            <phase>generate-sources</phase>
            <goals>
              <goal>run</goal>
            </goals>
            <configuration>
              <target name="install">
                <copy toDir="${basedir}/.git/hooks">
                  <fileset dir="${basedir}/.git-hooks/src/hooks/pre-push/maven">
                    <include name="pre-push"/>
                  </fileset>
                  <fileset dir="${basedir}/.git-hooks/src/hooks/pre-commit">
                    <include name="commit-msg"/>
                  </fileset>
                </copy>
                <chmod file="${basedir}/.git/hooks/pre-push" perm="ugo+x"/>
                <chmod file="${basedir}/.git/hooks/commit-msg" perm="ugo+x"/>
              </target>
            </configuration>
          </execution>
        </executions>
        <inherited>false</inherited>
      </plugin>
      ```
      
* android gradle

    ``` 
        tasks.create(name: 'gitExecutableHooks') {
            doLast {
                Runtime.getRuntime().exec("chmod -R +x .git/hooks/");
            }
        }
       task installGitHooks(type: Copy) {
           from new File(rootProject.rootDir, '.git-hooks/src/hooks/pre-push/android/pre-push')
                into { new File(rootProject.rootDir, '.git/hooks') }
        
           from new File(rootProject.rootDir, '.git-hooks/src/hooks/pre-commit/commit-msg')
               into { new File(rootProject.rootDir, '.git/hooks') }
        }
       gitExecutableHooks.dependsOn(installGitHooks)
       tasks.getByPath(':app:preBuild').dependsOn gitExecutableHooks
       ```

* Node
    - install husky plugin: https://www.npmjs.com/package/husky
    - install pretty-quick plugin: https://www.npmjs.com/package/pretty-quick 
    - Add the following snippet in package.json
        ```
         "husky": {
            "hooks": {
              "commit-msg": ".git-hooks/src/hooks/pre-commit/commit-msg ${HUSKY_GIT_PARAMS}",
              "pre-commit": "pretty-quick --staged",
              "pre-push": ".git-hooks/src/hooks/pre-push/npm/pre-push"
            }
          }
        ```    
    -  Run the following command on terminal:  ```chmod -R +x .git/hooks/```

* Python
    - Install yapf: ```pip install ypaf```
    - Add the following snippet in your package's main ```__init__.py```:
        ```
        from subprocess import call
        call(['cp', '.git-hooks/src/hooks/pre-push/python/<autopep8/ypaf>/pre-push', '.git/hooks/pre-push'])
        call(['cp', '.git-hooks/src/hooks/pre-commit/commit-msg', '.git/hooks/commit-msg'])
        ``` 
     - Add ```.yapfignore``` to add files/directories to ignore yapf formatting.

### Who do I talk to?
* [Ramesh Chandra](https://bitbucket.org/ramesh_rivigo)
* [Palash Goel](https://bitbucket.org/palashgoel7/)
* [JaiPrakash](https://bitbucket.org/jaiprakash_rivigo/)
* [Abhishek Tripathi](https://bitbucket.org/abhishektripathi_rivigo/)

### References
* https://www.atlassian.com/blog/git/alternatives-to-git-submodule-git-subtree
* https://medium.com/@porteneuve/mastering-git-subtrees-943d29a798ec