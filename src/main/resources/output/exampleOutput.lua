ScreenplayFactory:saveBuilder("intro", function()
    actorOvermind = ScreenplayFactory.createActor(udg_overmind)
    actorDuke = ScreenplayFactory.createActor(udg_duke)
    actorTassadar = ScreenplayFactory.createActor(udg_tassadar)

    return ScreenplaySystem.chain:buildFromObject({
        [1] = {
            actor = actorTassadar,
            text = "The Overmind have been weakened, but we have sustained severe damage ourselves.",
        },
        [2] = {
            actor = actorTassadar,
            choices = {
                [1] = {
                    text = "I will steer the Gantrithor on the collision course with the Overmind!",
                    onChoice = function()
                        ScreenplaySystem:currentItem().choices[1].visible = false
                        ScreenplaySystem:goTo(TODO)
                    end
                },

                [2] = {
                    text = "Time to get out of here!",
                    onChoice = function()
                        ScreenplaySystem:currentItem().choices[2].visible = false
                        ScreenplaySystem:goTo(TODO)
                    end
                },

            },
        },
        [3] = {
            actor = actorTassadar,
            text = "I will steer the Gantrithor on the collision course with the Overmind!",
        },
        [4] = {
            actor = actorOvermind,
            text = "Oh fuck!",
        },
        [5] = {
            actor = actorDuke,
            text = "That's what I call tactics, boy!",
        },
        [6] = {
            actor = actorTassadar,
            text = "Time to get out of here!",
        },

    })
end)
