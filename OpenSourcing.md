## Completed Tasks

 - Fixed all tests.
 - Created Travis Build, although this is using robmoffat fork.
 - Released to bintray.  This involved creating the deutschebank organisation there.
 - Created codecov coverage, again using robmoffat fork.
 - Applied for com.db.symphonyp namespace in sonatype staging repo (https://issues.sonatype.org/browse/OSSRH-52578) 
 - As requested, renamed to com.github.deutschebank.symphony
 - Have been granted com.github.deutschebank.  (not what I asked for...)
 - Installed MacGPG (In order to sign the releases) (see https://www.eviltester.com/2016/10/how-to-create-and-release-jar-to-maven.html)
    - Created and uploaded a public key.
      - gpg --gen-key
      - gpg --keyserver hkp://pool.sks-keyservers.net --send-keys 1DE5955674C2DB6B
      - configured key in settings.xml
 - Added links to shields 
 - Added spring boot components
 - Added tutorials
 - Added Seed apps

## Remaining Tasks

 - Get license team to review contributor licenses
 - Move bintray deutschebank organisation to be owned by DB OSRC team members
 - Must add other db users to sonatype
 - Add maven central chiclet
 - Get travis approved for DB organisation
 - Add cla-assistant approval for DB organisation
 - Add review for master change on github project
 - Remove self as admin on github
 - Remove self as admin on bitbucket
 - Get ramdas to review
 - Show+Tell
 - JSON Fix/JSON->MessageML



