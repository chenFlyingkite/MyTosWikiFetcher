package main.misc;

import flyingkite.log.L;

public class Mbo {
    public static void score() {
        // {[d1, d2, ..., dn], ...}
        // ans = d1 * d2 * ... * dn
        double[][] val = {{0.4, 0.25},{0.4, 0.35},{0.4, 0.40},
                {0.3, 0.25},{0.3, 0.45},{0.3, 0.30},
                {0.1, 1.0},
                {0.2, 0.40},{0.2, 0.35},{0.2, 0.25},
        };
        for (int i = 0; i < val.length; i++) {
            double[] d = val[i];
            double ans = 1F;
            for (int j = 0; j < d.length; j++) {
                ans *= d[j];
            }
            L.log("#%s : %2.3f", i, ans * 100);
        }
    }
    /*
    Planning Confirmed by Executor: Eric Chen(2021/1/11  12:16)
    Planning Confirmed by Manager: Todd Chen(2021/1/26  13:57)
    Evaluation Confirmed by Executor: Eric Chen(2021/7/16  16:58)
    Evaluation Confirmed by Manager: Todd Chen(2021/7/21  12:07)
1.	Objective	DueDate	Weight%(A)	Achievement %(B)	Eval. Comment	Score
            (A*B)
    Engaged in development of project to add features and fix bugs

    Photo Director - iOS	2021/6/30	40 %	93%	Good	37.2
            93%	Mainly focus on main features of March & April, Light Hit & Surreal Art
    Also self-study GPUImage and shader languages

            1. Getting familiar with
1-1> the structure
1-2> framework of The Project
2. Creating good structure for better maintenance	Fixing the eBugs and adding feature to ensure The Project follows the schedule on time
    Simply describe the developing monuments.	Very good	2021/6/30	25%	100%

    Implemented features and items (Order by time)
    Overall
    Organize FLUtility family and extend capability
    Fix misc UI/UX issues and preformance issues

2021/01
    Add Edit page each page's Call-To-Action when user using premium items
    Light hit 1st version capability study (Swift)
    Self-study GPUImage framework, and study shade language code for core of image processing

2021/02
    Light Hit implement/adjustment to fit spec , includes querying server content, memory issues

2021/03
    Correct Light Hit crashlytics
    Surreal art 1st version capability development

2021/04
    Implement Surreal Art details to fit spec and correct performance & memory issues
    Implement CMSAPI for PHD iOS, and make surreal art use the CMSAPI

2021/05
    Opening tutorial version 2 recreated, and refector & implement the new mechanism
2021/05/w3 start to work from home
    Update opening tutorial videos/ strings
    Add splash birdview

2021/06
    Android : Implemennt Setting page IAP big banner
    iOS : Implemennt Setting page IAP big ba	100%
            1. Fixing eBugs of The Project
1-1> with quality
1-2> and time efficiency.	Fixing the eBugs of The Project with quality, time efficiency and no severe side effect so as to ensure The Project follows the schedule on time	Good	2021/6/30	35%	80%
    see 1-1	80%
            1. Adding the required features
1-1> with quality
1-2> and efficiency	Fix the critical/blocking ebugs and enhance user experience of the new added features	Very good	2021/6/30	40%	100%
    see 1-1	100%


            2.	Objective	DueDate	Weight%(A)	Achievement %(B)	Eval. Comment	Score
            (A*B)
    Dynamically Support other project development for those in need.

            ActionDirector - Android	2021/6/30	30 %	90%	Good	27
            90%	Mainly support for PhotoDirector Android, and as consult for ActionDirector Android

    Item	Key Result	Evaluation
    Comment	Due
    Date	Weight
%	Achieve
    ment%
    Getting familiar with the overall structure of the project supporting.	Fixing the eBugs and adding feature to ensure supported project follows the schedule on time
    Simply describe the developing monuments.	Good	2021/6/30	25%	80%

    Implemented features and items (Order by time)
    Overall
    Organize FLUtility family and extend capability
    Fix misc UI/UX issues and preformance issues

2021/01
    Add Edit page each page's Call-To-Action when user using premium items
    Light hit 1st version capability study (Swift)
    Self-study GPUImage framework, and study shade language code for core of image processing

2021/02
    Light Hit implement/adjustment to fit spec , includes querying server content, memory issues

2021/03
    Correct Light Hit crashlytics
    Surreal art 1st version capability development

2021/04
    Implement Surreal Art details to fit spec and correct performance & memory issues
    Implement CMSAPI for PHD iOS, and make surreal art use the CMSAPI

2021/05
    Opening tutorial version 2 recreated, and refector & implement the new mechanism
2021/05/w3 start to work from home
    Update opening tutorial videos/ strings
    Add splash birdview

2021/06
    Android : Implemennt Setting page IAP big banner
    iOS : Implemennt Setting page IAP big ba	80%
    Fixing the eBugs or performing some unit test if the project requires or demands.	Fixing eBugs with quality and no side effect if any eBugs is fixed.	Very good	2021/6/30	50%	100%
    see 2-1	100%
    Adding new feature if the project requires or demands.	Adding new feature with quality and time efficiency if new feature is added.
    Enhance the user experience on the new/existing features	Good	2021/6/30	25%	80%
    see 2-1	80%


            3.	Objective	DueDate	Weight%(A)	Achievement %(B)	Eval. Comment	Score
            (A*B)
    Study / training technical related materials	2021/6/30	10 %	60%	OK	6
            100%	Provide the recharge program

    Item	Key Result	Evaluation
    Comment	Due
    Date	Weight
%	Achieve
    ment%
    Study either books, developer website or other materials	Share organized content by creating wiki page / PPT, provide production-ready codes to SVN or sending emails to coworkers or team members	OK	2021/6/30	100%	60%
    Eric Chen: UX Program - 2021/05/13
    PPT
    Video : P:\PhotoDirector_Touch\Recharge\UX Program_EricChen\[Recharge] UX Program - Eric Chen 2021-05_2021-05-13_18-37-51.mp4	100%


            4.	Objective	DueDate	Weight%(A)	Achievement %(B)	Eval. Comment	Score
            (A*B)
    Improvement of every project communication with main RDs, Managers, PMs, Arts, Server team, and other related staff.	2021/6/30	20 %	88%	Good	17.6
            88%	Discuss with android / ios team members and provide solution / idea

    Item	Key Result	Evaluation
    Comment	Due
    Date	Weight
%	Achieve
    ment%
            1. Receive confirmation on items assigned from coworkers / new self-dedication
1>1 new features
1>2 Bug fixes and expected results
1>3 Request with priority with upper managers.	Let coworkers receive efficient, effective, well-organized content includes
1. Schedules and planing
2. currect self tasks and concurrent staff tasks
3. Mainly goal and expected behaviors
4. Code quality	Good	2021/6/30	40%	80%
    Be familiar with Java, Kotlin, Objective C, and Swift syntax
80%
        1. Provide synchonized communication on items assigned from coworkers / new self-dedication
1>1 new features
1>2 Bug fixes and expected results
1>3 Request with priority with upper managers.	Let coworkers receive efficient, effective, well-organized content includes
1. Schedules and planing
2. currect self tasks and concurrent staff tasks
3. Mainly goal and expected behaviors
4. Code quality	Good	2021/6/30	40%	100%
    Discuss with team members with issue & solutions and provide mechanism easy to maintain and prevents possible pitfalls
100%
        1. Provide self-expression and opinion if new UX / UI idea when communications	Let coworkers receive efficient, effective, well-organized content includes
1. Core soul of idea
2. Abstract Implementation way
3. Schedule prediction
4. Estimatable results	Good	2021/6/30	20%	80%
    Add comment and documents for main engines and those core part	80%


            5.	Objective	DueDate	Weight%(A)	Achievement %(B)	Eval. Comment	Score
            (A*B)
    Keep self be good in Computer Science basic and English ability.	2021/6/30	0 %	80%	OK	0
            80%	The capability is in good state and still nee to take care health state both with CS basics

    Item	Key Result	Evaluation
    Comment	Due
    Date	Weight
%	Achieve
    ment%
    In this year, make myself always keep good in CS foundations and advanced skills	Using online materials to evaluate myself.	OK	2021/6/30	40%	80%
            0624LC597,237,311,49
            0712LC619,249,321,49	80%
    In this year, make myself in good English speaking / discussion and aim at discussing code / implementation ideas in full English (like talk with RDs uses only English) with no obstacles and no misunderstandings.	Using online materials to evaluate myself.	OK	2021/6/30	30%	80%
    Spend too less time on self-training and figuring out solutions	80%
    In this year, keep myself in fast to provide the concrete implementation codes with well-visual commentted and designing ideas with good discussions	Using online materials to evaluate myself.
    And commit concreate contents to svn	OK	2021/6/30	30%	80%
    Need to have a more intensive and effective training. Still need to have more better periodical training & life	80%


            6.	Objective	DueDate	Weight%(A)	Achievement %(B)	Eval. Comment	Score
            (A*B)
    Being able to handle different co-working RDs / managers personality and provide good impact on working efficiency on work.	2021/6/30	0 %	100%	OK	0
            100%	Acceptable

    Item	Key Result	Evaluation
    Comment	Due
    Date	Weight
%	Achieve
    ment%
    Make myself to be able to fit into teams by keeping my characteristics and team styles, even if the team is not my ideal team.	Be fast / self-motivated to suit into teams even if team manager is not assigning tasks and give ambiguous navigation and guidance on projects.
    Decrease personal discussion, understanding of staff if staff and I still failed to find a good way / protocols of negotiation, keep in mind that keep personal and company affairs separated.
    OK	2021/6/30	40%	100%
            .	100%
    Consider to propose transfer team request by myself if self and team fails to keep balancing.
            (for item 1)	No mixing personal affairs and workplace affairs into complicated situations.
    Keep works on, no personal emotions, judgements and any unfair items on staffs to have bad impact on work.
            OK	2021/6/30	30%	100%
            .	100%
    Consider to Do As Romans Do if new team have special characteristics and keep self in ideal state (for item 2)	Contribute usable items and good investigations for work and staff communications.
    Even if myself is in a difficult environments of team and members, still self-motivated to find / research new materials and have concrete result by leaving as code or comments.
            OK	2021/6/30	30%	100%
            .	100%


    Total Score : 87.8
    Contributions/Improvements
1. Major contributions or improvements for the past 6 months :
    Reviewed Staff	Reviewing Manager
1.	2021/01
    Add Edit page each page's Call-To-Action when user using premium items
    Light hit 1st version capability study (Swift)
    Self-study GPUImage framework, and study shade language code for core of image processing
2.	2021/02
    Light Hit implement/adjustment to fit spec , includes querying server content, memory issues
3.	2021/03
    Correct Light Hit crashlytics
    Surreal art 1st version capability development
4.	2021/04
    Implement Surreal Art details to fit spec and correct performance & memory issues
    Implement CMSAPI for PHD iOS, and make surreal art use the CMSAPI
5.	2021/05
    Opening tutorial version 2 recreated, and refector & implement the new mechanism
2021/05/w3 start to work from home
    Update opening tutorial videos/ strings
    Add splash birdview
6.	2021/06
    Android : Implemennt Setting page IAP big banner
    iOS : Implemennt Setting page IAP big banner
    Android : Add the send feedback of user subscribe state
7.	2021/07
    Android : Implemennt ShutterStock dlc version 2 (to extend after current items)
    FaceMeSDK Android : Implement 1st page of login meeting
    FaceMeSDK iOS : Implement 1st page of login meeting
8.	Overall
    Organize FLUtility family and extend capability
    Fix misc UI/UX issues and preformance issues
2. Items requiring more improvements over the past 6 months:
    Reviewed Staff	Reviewing Manager
1.	Improvement of every project communication with main RDs, Managers, PMs, Arts, Server team, and other related staff.
            2.	Take care health state of self, including rest, exercising, recreation and working.
3.	Keep self be good in Computer Science basic and English ability.
Comment :

1. Good contribution on implementing two major features of PHDI, LightHits and Surreal Art. Both of them are required solid understanding on GPUImage framework. You were aggressive and were working very hard for this technical challenge to deliver them on time.

2. Please consider to do more practices to clearly explain the architecture of your code to other members. It would be very helpful to the technical discussion and you can learn more ideas from other members.

3. Let's take care your health state and keep positive attitude on every challenge.

4. Your second part of recharge for UX is not completed yet and you did not book time to do it. This is bad.

5. Thank you for your open mind to support FaceMe Fintech project. It is also your show time to show that you can develop both Android project and iOS project. This is amazing!

( by Todd Chen 2021/7/21  12:07)
Fill in evaluation comment

( by Eric Chen 2021/7/16  16:58)
Main objectives:
1. PhD-iOS new feature development with good coding architecture and with good quality.
2. Dynamically support PhD-iOS UX/MX tasks.
3. Share Android/iOS programming experience to help other members to grow.

( by Todd Chen 2021/1/26  13:57)
1st version

( by Eric Chen 2021/1/11  12:16)
        */
/*

Mainly focus on main features of
1. Setting page IAP big banner
2. ShutterStock dlc v2
3. New style Tutorial page page, Image Quality page
4. Adding Flurry / other logging / fabrics events
5. Miscellaneous and time-tight request for tasks.





----------

Implemented features and items (Order by time)

Overall
1. Setting page IAP big banner
2. ShutterStock dlc v2
3. New style Tutorial page page, Image Quality page
4. Adding Flurry / other logging / fabrics events
5. Miscellaneous and time-tight request for tasks.
6. Fix misc UI/UX issues and performance issues


2021/06
Android : Implement Setting page IAP big banner
iOS : Implement Setting page IAP big banner
2021/06/08
Android : [212603] was requested by Manager to revert all the work on IAP big banner.
2021/06/23
iOS : [7271] was requested by Manager to revert all the work on IAP big banner.
2021/06
iOS : [7499, 7524] Adjust surreal art for new content support

2021/06/30
iOS : [7602] was request to recover all IAP big banner feature back within one day to bring every hardwork back.
iOS : make Mac can both run on PhotoDirector iOS, FaceMe SDK and Fintech simultaneously in provisioning profiles.

2021/07
iOS : Fix bug from IAP big banner, only have less than 5 bugs (including regressed).
iOS : Fix fabric crashes in surreal art
iOS : Surreal art feature UI update
iOS : Implement Subscribe user's specified strings (features in 8/E)
Android : [212928] was request to recover all IAP big banner feature back within one day to bring every hardwork back.
Android : Add switch for each row in setting page
Android : Correct crashes in save location
Android : Implement Shutterstock DLC v1 and append it after v0, also send the shuttter stock id when user select image and then produce
Android : Change setting page's photo quality order as PM request

2021/08
iOS : keep in ready state.
Android : Implement Shutterstock v1 ready!, aroused less than 5 bugs (including regressed) with over 5k character change
Android : Add the subscribe after tryout events of flurry for every features
(of "Live Living", "Change Background", "Surreal Art", "Face Shaper", "Object Removal", "Live Firework", "Wraparound", "Sky Replacement", "Live Vacation", "Animated Decor")
Android : Change Image Quality page style including refactor layouts and features, like adding top banner
Android : Add the subscribe after tryout events of flurry for every features (of "Sparkle", "Bokeh", "Animated Overlay")
Android : Add shutterstock log event and send id
Android : Refactor and implement for Tutorial page to boost code readability and performance
Android : Add contents of old surreal art & log events of imporession & click
Android : Add tutorial purchase event for log purchase event
Android : Fix bugs of new tutorial page to fit art spec

2021/09
was requested to completely support FaceMe team.
and has no assigned task.

2021/10
iOS : keep in late ready
Android : Correct the shutter stock dlc 2 images cannot download issues

2021/11
iOS : Add Uma log for Getty image when getty image has triggered downloading image
iOS : Change send feedabck email field hint text from "email" to "what's your e-mail address?"
iOS : Change text of add photo's discard dialog to be keep/discard and make it destructive
iOS : Add the timezone for Getty image uma log
iOS : Change permission remind dialog as ios style

2021/11/15
Transferred to RD-FM team as development member of FaceMe SDK / Fintech

------
see 1-1
------
see 1-1
------
Overall
Complete the Fintech layouts and all flows and features from meeting, including landscape/portrait, misc devices, and overall flow
Complete the Fintech Android and Fintech iOS project to make it deliver on time and quality controlled
------

2021/06/18
FaceMeSDK Android : [15216] 1st commit for entry point of Remote Contract as a starter for Fintech
2021/06/24
FaceMeSDK iOS : [15289] 1st commit for entry point of Remote Contract as a starter for Fintech
2021/06
FaceMeSDK Android : Add meeting + ekyc page of layouts, images resources, and its required features
FaceMeSDK iOS : Add meeting + ekyc page of layouts, images resources, and its required features

2021/07
FaceMe Fintech Android : Update UI layouts and features to memorize last time entered meeting info
FaceMe Fintech iOS : Update UI layouts and features to memorize last time entered meeting info
FaceMeSDK Android : Add meeting server ip default value
FaceMeSDK Android : Open path for EKYC, and add icons for app
FaceMeSDK iOS : Adjust layout of KYCsetting button to fit spec (spec keeping change every week)
FaceMeSDK iOS : Add main engine of Remote contract and implement related code
FaceMeSDK iOS : Change image icon for app

2021/08
FaceMe Fintech Android : Implement Milestone 3 UI layotus and features
FaceMe Fintech Android : Correct mechanism of show/hide the local video when desktop sharing
FaceMe Fintech Android : Correct misc UI bugs of showing local videos, Also make UI sync to spec M3
FaceMe Fintech Android : Adjust UI layout to show up Milestone 2 UI layouts as request
FaceMe Fintech Android : Move mini mode button and others in proper layout place to prevent overlapping or other issues
FaceMe Fintech Android : Add responsive background for logo, to hint click, and icon of fintech
FaceMe Fintech Android : Adjust UI layout for login page and meeting page, to make it ok for pad and phone, also for orientation changed
FaceMe Fintech Android : Project management by reside from MeetingSDK to app
FaceMe Fintech Android : Implement features of back, minimode, logo button behaviors as request / spec changed
FaceMe Fintech Android : Implement the landscape and portrait shows different sample contract image
FaceMe Fintech iOS : Implement Milestone 3 UI layotus and features
FaceMe Fintech iOS : Implement login page title and correct MUI issue
FaceMe Fintech iOS : Change the enter ip, name, and refactor to simplify login page's layout constraints
FaceMe Fintech iOS : Move mini mode button and others in proper layout place to prevent overlapping or other issues
FaceMe Fintech iOS : Apply art UI for minimode, and make UI suitable for all iphones and iPads to fit in safe area
FaceMe Fintech iOS : Add Sign Document entry
FaceMeSDK Android : No task assigned, all tasks goes well on my owns, be ready to assigned task,
FaceMeSDK iOS : No task assigned, all tasks goes well on my owns, be ready to assigned task,

2021/09
FaceMe Fintech Android : Adjust the layout / image icon / styles for fintech pages
FaceMe Fintech Android : Correct layouts / image icons / styles for share desktop, meeting, and minimode
FaceMe Fintech Android : Add send app log for android
FaceMe Fintech Android : Start to implement the meeting layout of FintechLayout class for (1,2,3,4 people)x(Landscape/portrait)x(Full/mini)x(shareDesktop/meeting) layouts
FaceMe Fintech Android : Cut API of void onDisplayMode(boolean isMiniMode, Rect insect);
FaceMe Fintech Android : Perform special layout for the minimode of 4 people
FaceMe Fintech Android : Show layout for the share desktop & minimode
FaceMe Fintech Android : Add the string of share desktop, meeting, ekyc and others
FaceMe Fintech Android : Show desktop sharing info when show and confirmed
FaceMe Fintech iOS : No task assigned, all tasks goes well on my owns, be ready to assigned task,
FaceMeSDK Android : No task assigned, all tasks goes well on my owns, be ready to assigned task,
FaceMeSDK iOS : No task assigned, all tasks goes well on my owns, be ready to assigned task,

2021/10
FaceMe Fintech Android : Show toast for someone is sharing desktop when minimode is click when in sharing
FaceMe Fintech Android : Move images / buttons and others in proper place to prevent overlapping or other issues
FaceMe Fintech Android : Meeting layout main structure is ready, Condense the meeting layout of FintechLayout class for (1,2,3,4 people)x(Landscape/portrait)x(Full/mini)x(shareDesktop/meeting) layouts
FaceMe Fintech Android : No show up the dialog of 20 min again when click hang up
FaceMe Fintech Android : Returns zero position if view is gone for onDisplayInfoUpdate()
FaceMe Fintech Android : Make send app log in UMeeting to be easy to click by larger height
FaceMe Fintech Android : Correct that speaker did not shows up when in mini mode
FaceMe Fintech Android : Make the bottom bar layout same style as ios in landscape
FaceMe Fintech Android : Correct the contract image will be shifted when in landscape mode in miniMode
FaceMe Fintech Android : Add logs for FintechLayouts.java & NileNetworks's callbacks into UUtilityLogs
FaceMe Fintech iOS : Make the meeting duraiton text back ground one no show up
FaceMe Fintech iOS : Shows the 6-th user cannot join the meeting since we are limit the meeting to be at most 5 people simultaneouly in meeting.
FaceMe Fintech iOS : Show back the switch camera button when in receiving other's sharing desktop
FaceMeSDK Android : No task assigned, all tasks goes well on my owns, be ready to assigned task,
FaceMeSDK iOS : No task assigned, all tasks goes well on my owns, be ready to assigned task,

2021/11
FaceMe Fintech Android : Change the image of lite mode for recording icon
FaceMe Fintech Android : Make the share screen's video screen appears, since the constraint layouts children missing some constraints on layouts so it make size on (0, 0)
FaceMe Fintech Android : Correct FintechLayouts for preventing to be in square when rotate from portrait to landscape
FaceMe Fintech Android : Change default serverIP
FaceMe Fintech Android : Add logs and print returned body for sending request to server
FaceMe Fintech iOS : No task assigned, all tasks goes well on my owns, be ready to assigned task,
FaceMeSDK Android : Implement as Activity for Make the eKYC Demo flow to see the setting and activity, no see fragment detached and menu, then see the activity
FaceMeSDK Android : Correct the flow of ekyc demo > ID/Passport > change any or back > will finish the fragment
FaceMeSDK Android : Less margin left/right for e-passport texts titles & descriptions
FaceMeSDK iOS : No task assigned, all tasks goes well on my owns, be ready to assigned task,


2021/12
FaceMe Fintech Android : Make ekyc date format of birth date and expiry date to be format of "dd MMM yyyy" not "MM.dd.yyyy"
FaceMe Fintech Android : Update the tv wall when ekyc is gone and back to meeting from portrait
FaceMe Fintech Android : Add the translucent background for Connecting...
FaceMe Fintech Android : Apply the fast solution for action on minimode button
FaceMe Fintech iOS : No task assigned, all tasks goes well on my owns, be ready to assigned task,
FaceMeSDK Android : Make birth date to be "dd MMM yyyy", not MM.dd.yyyy
FaceMeSDK iOS : No task assigned, all tasks goes well on my owns, be ready to assigned task,

2022/01
FaceMe Fintech Android : Make the Fintech Video Screen layouts fits for tablets and different screen ratios
FaceMe Fintech Android : Correct the issue of Pixel 6 / Pixel 6 pro cannot see other's video screen
FaceMe Fintech Android : Make left/right spacing less for more space for text on the ekyc's backIdCard
FaceMe Fintech iOS : No task assigned, all tasks goes well on my owns, be ready to assigned task,
FaceMeSDK Android : No task assigned, all tasks goes well on my owns, be ready to assigned task,
FaceMeSDK iOS : No task assigned, all tasks goes well on my owns, be ready to assigned task,
------
see 2-1
------
see 2-1
------
was not requested to provide new content, still use original existing contents, like chromebook debug toolkit
------
was not requested to provide new content, still use original existing contents, like chromebook debug toolkit
------
Discuss with android / ios team members and provide solution / idea
------
Be familiar with Java, Kotlin, Objective C, and Swift syntax
------
Discuss with team members with issue & solutions and provide mechanism easy to maintain and prevents possible pitfalls
------
Add comment and documents for main engines and those core part
------
The capability is in good state and still need to take care health state both with CS basics
And the test result is good enough, but still not pass the expected quality.
------
0929LC123,72,87
1001LC120,82,82
1003LC250,338,49
1223LC267,358,52
15M30M40M10M5M3E3E5E10E7E20M
------
ASDPHTDBFSSGBTMTPBMSDHPQBTGSPSSWLLCUFRBSTTMSMGSM.B
------
09301015102010291102111611171124
------
TSDXNMTSDXNMTSXNMTSXNMTSNMTSNMTSHSNM
------
Mainly focus on main features of
on PhotoDirector iOS
1. Setting page IAP big banner
2. ShutterStock dlc v2
3. New style Tutorial page page, Image Quality page
4. Adding Flurry / other logging / fabrics events
5. Miscellaneous and time-tight request for tasks.
------
Improvement of every project communication with main RDs, Managers, PMs, Arts, Server team, and other related staff.
------
Take care health state of self, including rest, exercising, recreation and working.
------
Keep self be good in Computer Science basic and English ability.

*/
}
