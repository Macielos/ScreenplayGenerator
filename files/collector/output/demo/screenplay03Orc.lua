ScreenplayFactory:saveBuilderForMessageChain("orc", function()
    local sounds = {}
    sounds.grunt01 = gg_snd_GruntWhat2
    sounds.footman01 = gg_snd_FootmanWarcry1

    return {
        [1] = {
            text = ScreenplayMessages['orc001'],
            sound = ScreenplaySounds['orc001'],
            actor = actorFootman,
        },
        [2] = {
            delayText = 3.0,
            text = ScreenplayMessages['orc002'],
            sound = ScreenplaySounds['orc002'],
            actor = actorGrunt,
            anim = "stand two",
            sound = sounds.grunt01,
        },
        [3] = {
            actor = actorFootman,
            choices = {
                [1] = {
                    text = ScreenplayMessages['orc003_001'],
                    onChoice = function()
                        ScreenplaySystem:currentItem().choices[1].visible = false
                    end,
                    onChoiceGoTo = 4
                },
                [2] = {
                    text = ScreenplayMessages['orc003_002'],
                    onChoiceGoTo = 7
                },
                [3] = {
                    text = ScreenplayMessages['orc003_003'],
                    onChoice = function()
                        ScreenplaySystem:currentItem().choices[3].visible = false
                        ScreenplaySystem:currentItem().choices[4].visible = true
                    end,
                    onChoiceGoTo = 9
                },
                [4] = {
                    text = ScreenplayMessages['orc003_004'],
                    visible = false,
                    onChoice = function()
                        ScreenplaySystem:currentItem().choices[4].visible = false
                    end,
                    onChoiceGoTo = 11
                },
                [5] = {
                    text = ScreenplayMessages['orc003_005'],
                    onChoiceGoTo = 19
                }
            }
        },
        [4] = {
            text = ScreenplayMessages['orc004'],
            sound = ScreenplaySounds['orc004'],
            actor = actorFootman,
            sound = sounds.footman01
        },
        [5] = {
            text = ScreenplayMessages['orc005'],
            sound = ScreenplaySounds['orc005'],
            actor = actorPeon,
            trigger = gg_trg_Scene_Orc_Peon_Escape
        },
        [6] = {
            text = ScreenplayMessages['orc006'],
            sound = ScreenplaySounds['orc006'],
            actor = actorGrunt,
            thenGoTo = 3
        },
        [7] = {
            text = ScreenplayMessages['orc007'],
            sound = ScreenplaySounds['orc007'],
            actor = actorFootman,
            thenGoToFunc = function()
                if sceneOrcAlreadyAskedAboutWares == true
                then
                    return 18
                else
                    sceneOrcAlreadyAskedAboutWares = true
                    return 8
                end
            end
        },
        [8] = {
            text = ScreenplayMessages['orc008'],
            sound = ScreenplaySounds['orc008'],
            actor = actorGrunt,
            thenGoTo = 3
        },
        [9] = {
            text = ScreenplayMessages['orc009'],
            sound = ScreenplaySounds['orc009'],
            actor = actorFootman,
        },
        [10] = {
            text = ScreenplayMessages['orc010'],
            sound = ScreenplaySounds['orc010'],
            actor = actorGrunt,
            thenGoTo = 3,
        },
        [11] = {
            text = ScreenplayMessages['orc011'],
            sound = ScreenplaySounds['orc011'],
            actor = actorFootman,
        },
        [12] = {
            text = ScreenplayMessages['orc012'],
            sound = ScreenplaySounds['orc012'],
            actor = actorGrunt,
        },
        [13] = {
            actor = actorFootman,
            choices = {
                [1] = {
                    text = ScreenplayMessages['orc013_001'],
                    onChoiceGoTo = 14
                },
                [2] = {
                    text = ScreenplayMessages['orc013_002'],
                    onChoiceGoTo = 16
                },
            }
        },
        [14] = {
            text = ScreenplayMessages['orc014'],
            sound = ScreenplaySounds['orc014'],
            actor = actorFootman,
        },
        [15] = {
            text = ScreenplayMessages['orc015'],
            sound = ScreenplaySounds['orc015'],
            actor = actorGrunt,
            thenEndScene = true,
            trigger = gg_trg_Scene_Orc_Follow,
        },
        [16] = {
            text = ScreenplayMessages['orc016'],
            sound = ScreenplaySounds['orc016'],
            actor = actorFootman,
        },
        [17] = {
            text = ScreenplayMessages['orc017'],
            sound = ScreenplaySounds['orc017'],
            actor = actorGrunt,
            thenGoTo = 3
        },
        [18] = {
            text = ScreenplayMessages['orc018'],
            sound = ScreenplaySounds['orc018'],
            actor = actorGrunt,
            thenGoTo = 3,
        },
        [19] = {
            text = ScreenplayMessages['orc019'],
            sound = ScreenplaySounds['orc019'],
            actor = actorFootman,
        },
        [20] = {
            text = ScreenplayMessages['orc020'],
            sound = ScreenplaySounds['orc020'],
            actor = actorGrunt,
        },
    }
end)
